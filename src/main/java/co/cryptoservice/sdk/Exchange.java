// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * One logical call: its attempts, the waits between them, and its deadline.
 *
 * <p>{@link #result} completes with the last response (any status) or a {@link TransportError}.
 * Whatever completes it first wins: an attempt's outcome, the deadline timer, or the caller
 * cancelling it. Every attempt in flight and every scheduled retry or deadline is cancelled when it
 * completes, which aborts the HTTP exchange; an outcome that arrives after that is dropped.
 */
final class Exchange {

  private final Transport transport;
  private final Route route;
  private final byte[] body;
  private final String contentType;
  private final long deadlineNanos;
  private final CompletableFuture<HttpResponse<byte[]>> result = new CompletableFuture<>();
  private int attempt;

  Exchange(Transport transport, Route route, byte[] body, String contentType) {
    this.transport = transport;
    this.route = route;
    this.body = body;
    this.contentType = contentType;
    Duration deadline = transport.deadline();
    this.deadlineNanos = deadline == null ? Long.MAX_VALUE : System.nanoTime() + deadline.toNanos();
  }

  /** Starts the first attempt and returns the call's future. */
  CompletableFuture<HttpResponse<byte[]>> start() {
    Duration deadline = transport.deadline();
    if (deadline != null) {
      cancelOnCompletion(
          Scheduler.after(
              deadline, () -> result.completeExceptionally(TransportError.deadline(route))));
    }
    send();
    return result;
  }

  private void cancelOnCompletion(Future<?> future) {
    result.whenComplete((response, error) -> future.cancel(false));
  }

  private void send() {
    attempt++;
    CompletableFuture<HttpResponse<byte[]>> inFlight =
        transport.sendAsync(transport.request(route, body, contentType));
    cancelOnCompletion(inFlight);
    inFlight.whenComplete(this::settle);
  }

  private void settle(HttpResponse<byte[]> response, Throwable error) {
    if (result.isDone()) {
      return;
    }
    if (error == null) {
      onResponse(response);
    } else {
      onFailure(Transport.unwrap(error));
    }
  }

  private void onFailure(Throwable cause) {
    Optional<Duration> wait = transport.backoff().afterFailure(attempt, route.idempotent());
    retryOr(wait, () -> result.completeExceptionally(TransportError.of(route, cause)));
  }

  private void onResponse(HttpResponse<byte[]> response) {
    int status = response.statusCode();
    transport.notify(
        new ResponseInfo(route.method(), route.path(), status, response.headers(), attempt));
    Optional<String> retryAfter = response.headers().firstValue("retry-after");
    Optional<Duration> wait =
        transport.backoff().afterStatus(attempt, status, route.idempotent(), retryAfter);
    retryOr(wait, () -> result.complete(response));
  }

  private void retryOr(Optional<Duration> wait, Runnable finish) {
    if (wait.isPresent() && System.nanoTime() + wait.get().toNanos() < deadlineNanos) {
      cancelOnCompletion(Scheduler.after(wait.get(), this::send));
    } else {
      finish.run();
    }
  }
}
