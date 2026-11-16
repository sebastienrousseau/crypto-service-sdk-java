// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * The plumbing every typed operation shares: encode the request, run the exchange, decode the
 * answer, and the blocking wait. The operation groups extend it; {@link CryptoServiceClient} is the
 * public end of the chain.
 */
abstract class ClientCore {

  final Transport transport;

  ClientCore(Transport transport) {
    this.transport = transport;
  }

  /** Runs a typed operation; the body is null only for {@code GET}. */
  final <T> CompletableFuture<T> call(Operation<T> op, Object body) {
    byte[] bytes = op.hasBody() ? Json.encode(Objects.requireNonNull(body, "request")) : null;
    return map(transport.exchange(op.route(), bytes, Json.MEDIA_TYPE), op::result);
  }

  /**
   * A future of {@code fn(source)} whose cancellation cancels {@code source}, so cancelling the
   * caller's future aborts the HTTP exchange.
   */
  static <A, B> CompletableFuture<B> map(CompletableFuture<A> source, Function<A, B> fn) {
    CompletableFuture<B> out = new CompletableFuture<>();
    source.whenComplete(
        (value, error) -> {
          if (error != null) {
            out.completeExceptionally(Transport.unwrap(error));
            return;
          }
          try {
            out.complete(fn.apply(value));
          } catch (RuntimeException e) {
            out.completeExceptionally(e);
          }
        });
    out.whenComplete((value, error) -> source.cancel(true));
    return out;
  }

  /**
   * Waits for an async call and returns its result, or throws what it failed with. An interrupt
   * cancels the call (aborting the HTTP exchange), restores the interrupt flag and raises a {@link
   * TransportError}.
   */
  static <T> T await(Route route, CompletableFuture<T> future) {
    try {
      return future.get();
    } catch (InterruptedException e) {
      future.cancel(true);
      Thread.currentThread().interrupt();
      throw TransportError.interrupted(route, e);
    } catch (ExecutionException e) {
      throw rethrow(e.getCause());
    }
  }

  /** What a blocking call throws for the cause of a failed future. */
  static RuntimeException rethrow(Throwable cause) {
    if (cause instanceof RuntimeException runtime) {
      return runtime;
    }
    if (cause instanceof Error error) {
      throw error;
    }
    return new IllegalStateException("the call failed", cause);
  }

  /** The answer to a raw request, whatever its status. */
  static RawResponse raw(HttpResponse<byte[]> response) {
    return new RawResponse(response.statusCode(), response.headers(), response.body());
  }
}
