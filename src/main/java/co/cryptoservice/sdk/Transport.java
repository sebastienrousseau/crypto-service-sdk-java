// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Everything a client instance shares between calls: the pooled HTTP client, the base URL, the
 * credentials, the timeouts, the retry policy, tracing and the response listener. Immutable apart
 * from the closed flag, so one instance serves any number of threads.
 */
final class Transport {

  private final HttpClient http;
  private final boolean ownsHttp;
  private final String baseUrl;
  private final String apiKey;
  private final String token;
  private final Duration timeout;
  private final Duration deadline;
  private final Backoff backoff;
  private final Supplier<String> traceparent;
  private final ResponseListener listener;
  private final AtomicBoolean closed = new AtomicBoolean();

  Transport(CryptoServiceClient.Builder b, HttpClient http, boolean ownsHttp) {
    this.http = http;
    this.ownsHttp = ownsHttp;
    this.baseUrl = b.baseUrl;
    this.apiKey = b.apiKey;
    this.token = b.token;
    this.timeout = b.timeout;
    this.deadline = b.deadline;
    this.backoff = new Backoff(b.retry);
    this.traceparent = b.traceparent;
    this.listener = b.listener;
  }

  Duration deadline() {
    return deadline;
  }

  Backoff backoff() {
    return backoff;
  }

  /** Starts a call; the future completes with the last response of any status. */
  CompletableFuture<HttpResponse<byte[]>> exchange(Route route, byte[] body, String contentType) {
    if (closed.get()) {
      throw new IllegalStateException("the client is closed");
    }
    return new Exchange(this, route, body, contentType).start();
  }

  CompletableFuture<HttpResponse<byte[]>> sendAsync(HttpRequest request) {
    return http.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
  }

  /** The request for one attempt; every attempt gets its own timeout and traceparent. */
  HttpRequest request(Route route, byte[] body, String contentType) {
    HttpRequest.BodyPublisher publisher =
        body == null
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofByteArray(body);
    HttpRequest.Builder builder =
        HttpRequest.newBuilder(URI.create(baseUrl + route.path()))
            .timeout(timeout)
            .header("User-Agent", CryptoServiceClient.USER_AGENT)
            .method(route.method(), publisher);
    if (body != null) {
      builder.header("Content-Type", contentType);
    }
    if (route.authenticated()) {
      credentials(builder);
    }
    if (traceparent != null) {
      builder.header("traceparent", traceparent.get());
    }
    return builder.build();
  }

  private void credentials(HttpRequest.Builder builder) {
    if (apiKey != null) {
      builder.header("x-api-key", apiKey);
    }
    if (token != null) {
      builder.header("Authorization", "Bearer " + token);
    }
  }

  /** Tells the listener, if any, about a response; its exceptions are ignored. */
  void notify(ResponseInfo info) {
    if (listener == null) {
      return;
    }
    try {
      listener.onResponse(info);
    } catch (RuntimeException ignored) {
      // A listener must not change the outcome of the call.
    }
  }

  /** Closes the HTTP client if this transport created it; idempotent. */
  void close() {
    if (closed.compareAndSet(false, true) && ownsHttp) {
      closeIfCloseable(http);
    }
  }

  /**
   * Closes {@code resource} when it is {@link AutoCloseable}: {@link HttpClient} is from Java 21,
   * and this library targets 17.
   */
  static void closeIfCloseable(Object resource) {
    if (resource instanceof AutoCloseable closeable) {
      try {
        closeable.close();
      } catch (Exception e) {
        throw new IllegalStateException("closing the HTTP client failed", e);
      }
    }
  }

  /** The exception inside a {@link CompletionException}, or the exception itself. */
  static Throwable unwrap(Throwable error) {
    return error instanceof CompletionException && error.getCause() != null
        ? error.getCause()
        : error;
  }
}
