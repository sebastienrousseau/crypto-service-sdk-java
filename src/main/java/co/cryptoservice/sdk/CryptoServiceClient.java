// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * A client for the Crypto Service REST API (crypto-server). Every operation is an HTTP call: the
 * SDK performs no cryptography itself.
 *
 * <pre>{@code
 * try (CryptoServiceClient client = CryptoServiceClient.builder()
 *     .baseUrl("http://127.0.0.1:3101")
 *     .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
 *     .build()) {
 *   HashResult hash = client.hash(new HashRequest(HashAlgorithm.SHA256, "abc"));
 *   CompletableFuture<HashResult> later =
 *       client.hashAsync(new HashRequest(HashAlgorithm.BLAKE3, "x"));
 * }
 * }</pre>
 *
 * <p>Every operation has an asynchronous form, {@code xxxAsync(...)}, returning a {@link
 * CompletableFuture}, and a blocking form, {@code xxx(...)}, that waits for it. Cancelling the
 * future ({@code cancel(true)}) aborts the HTTP exchange and any retry. Failures are an {@link
 * ApiError} (the server answered outside 2xx; it carries the RFC 9457 {@link Problem}) or a {@link
 * TransportError} (no usable answer).
 *
 * <p><b>Threads.</b> A client is immutable after {@link Builder#build()} and safe to share between
 * any number of threads; it holds one pooled {@link HttpClient} (HTTP/2 where the server offers it,
 * keep-alive connections), so build one per server and reuse it. The blocking forms park the
 * calling thread in {@link CompletableFuture#get()}, so they suit virtual threads (Java 21): a
 * virtual thread per call costs no platform thread while it waits.
 *
 * <p><b>Security.</b> Redirects are never followed (a 3xx is an {@link ApiError}), so credentials
 * cannot leak to another origin. TLS uses the JVM's default trust store and hostname verification;
 * the SDK has no option to turn either off. Credentials, keys, plaintexts and bodies never appear
 * in exception messages or in any {@code toString}.
 */
public final class CryptoServiceClient extends MetaOperations implements AutoCloseable {

  /** This SDK's version, sent as {@code User-Agent: crypto-service-sdk-java/<version>}. */
  public static final String VERSION = "0.0.40";

  static final String USER_AGENT = "crypto-service-sdk-java/" + VERSION;

  private CryptoServiceClient(Transport transport) {
    super(transport);
  }

  /**
   * Starts a builder; {@link Builder#baseUrl} is required.
   *
   * @return a new builder with the defaults
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Sends one request to any path and returns the answer whatever its status: the escape hatch for
   * routes the typed methods do not cover (for example the deprecated v1 routes or {@code
   * /metrics}). Credentials, the timeout, the deadline, retries and tracing apply as for typed
   * calls.
   *
   * @param method the HTTP method, for example {@code POST}
   * @param path the path below the base URL, starting with {@code /}
   * @param body the body, or null to send none
   * @param contentType the body's media type, or null for {@code application/json}
   * @return a future completed with the response, or exceptionally with a {@link TransportError}
   * @throws IllegalArgumentException when the path does not start with {@code /}
   */
  public CompletableFuture<RawResponse> requestAsync(
      String method, String path, byte[] body, String contentType) {
    Route route = rawRoute(method, path);
    String type = contentType == null ? Json.MEDIA_TYPE : contentType;
    return map(transport.exchange(route, body, type), ClientCore::raw);
  }

  /**
   * Sends one request and waits for the answer; see {@link #requestAsync}.
   *
   * @param method the HTTP method, for example {@code POST}
   * @param path the path below the base URL, starting with {@code /}
   * @param body the body, or null to send none
   * @param contentType the body's media type, or null for {@code application/json}
   * @return the response, whatever its status
   * @throws TransportError when no response arrives
   */
  public RawResponse request(String method, String path, byte[] body, String contentType) {
    return await(rawRoute(method, path), requestAsync(method, path, body, contentType));
  }

  private static Route rawRoute(String method, String path) {
    Objects.requireNonNull(method, "method");
    if (path == null || !path.startsWith("/")) {
      throw new IllegalArgumentException("path must start with /");
    }
    return Route.of(method.toUpperCase(Locale.ROOT), path, true);
  }

  /**
   * Stops accepting calls and, when the client created its own {@link HttpClient}, closes it (on
   * Java 21 and later, where {@code HttpClient} is {@link AutoCloseable}; on Java 17 its threads
   * end once it is unreachable). An injected {@code HttpClient} is left open. Idempotent.
   */
  @Override
  public void close() {
    transport.close();
  }

  /** Builds a {@link CryptoServiceClient}. Not thread-safe; the client it builds is. */
  public static final class Builder {

    String baseUrl;
    String apiKey;
    String token;
    Duration timeout = Duration.ofSeconds(30);
    Duration deadline;
    RetryOptions retry = RetryOptions.NONE;
    Supplier<String> traceparent;
    ResponseListener listener;
    private HttpClient httpClient;

    private Builder() {}

    /**
     * The server's base URL, for example {@code http://127.0.0.1:3101}: http or https, with a host,
     * and no user info, query or fragment; a path prefix is kept and a trailing slash dropped.
     *
     * @param baseUrl the base URL
     * @return this builder
     */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * An API key, sent as {@code x-api-key}; null sends none.
     *
     * @param apiKey the key, or null
     * @return this builder
     */
    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    /**
     * A JWT, sent as {@code Authorization: Bearer <token>}; null sends none.
     *
     * @param token the token, or null
     * @return this builder
     */
    public Builder token(String token) {
      this.token = token;
      return this;
    }

    /**
     * The timeout of each attempt (connect and response headers); 30 seconds when not set.
     *
     * @param timeout a positive duration
     * @return this builder
     */
    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    /**
     * The limit on a whole call, retries and waits included; none when not set. When it passes, the
     * call fails with a {@link TransportError} whose {@code timedOut()} is true.
     *
     * @param deadline a positive duration, or null for none
     * @return this builder
     */
    public Builder deadline(Duration deadline) {
      this.deadline = deadline;
      return this;
    }

    /**
     * Retries of transient failures; {@link RetryOptions#NONE} (the default) sends every call once.
     *
     * @param retry the retry options
     * @return this builder
     */
    public Builder retry(RetryOptions retry) {
      this.retry = Objects.requireNonNull(retry, "retry");
      return this;
    }

    /**
     * A fixed W3C {@code traceparent} sent with every request; null sends none.
     *
     * @param traceparent a {@code version-traceid-parentid-flags} value, or null
     * @return this builder
     * @throws IllegalArgumentException when the value is not a traceparent
     */
    public Builder traceparent(String traceparent) {
      String value = traceparent == null ? null : Traceparent.validate(traceparent);
      this.traceparent = value == null ? null : () -> value;
      return this;
    }

    /**
     * Sends a freshly generated W3C {@code traceparent} with every request (true), or none (false).
     *
     * @param enabled whether to generate one per request
     * @return this builder
     */
    public Builder autoTraceparent(boolean enabled) {
      this.traceparent = enabled ? Traceparent.generator() : null;
      return this;
    }

    /**
     * Observes every response's status and headers; null for none.
     *
     * @param listener the listener, or null
     * @return this builder
     */
    public Builder responseListener(ResponseListener listener) {
      this.listener = listener;
      return this;
    }

    /**
     * An {@link HttpClient} to send through instead of the client's own, for tests or a custom
     * proxy or executor. It must not follow redirects. Its TLS settings are the caller's
     * responsibility, and {@link CryptoServiceClient#close()} leaves it open.
     *
     * @param httpClient the HTTP client, or null for the client's own
     * @return this builder
     */
    public Builder httpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    /**
     * Builds the client.
     *
     * @return the client, thread-safe
     * @throws IllegalArgumentException when the base URL is missing or malformed, a duration is not
     *     positive, a credential is not a valid header value, or the injected HTTP client follows
     *     redirects
     */
    public CryptoServiceClient build() {
      baseUrl = normalise(baseUrl);
      requirePositive(timeout, "timeout");
      if (deadline != null) {
        requirePositive(deadline, "deadline");
      }
      checkHeaders();
      boolean own = httpClient == null;
      HttpClient http = own ? defaultHttpClient(timeout) : checked(httpClient);
      return new CryptoServiceClient(new Transport(this, http, own));
    }

    private void checkHeaders() {
      HttpRequest.Builder probe = HttpRequest.newBuilder(URI.create(baseUrl));
      if (apiKey != null) {
        probe.header("x-api-key", apiKey);
      }
      if (token != null) {
        probe.header("Authorization", "Bearer " + token);
      }
    }

    private static HttpClient defaultHttpClient(Duration timeout) {
      return HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .followRedirects(HttpClient.Redirect.NEVER)
          .connectTimeout(timeout)
          .build();
    }

    private static HttpClient checked(HttpClient http) {
      if (http.followRedirects() != HttpClient.Redirect.NEVER) {
        throw new IllegalArgumentException("the HTTP client must not follow redirects");
      }
      return http;
    }

    private static void requirePositive(Duration value, String name) {
      if (value == null || value.isZero() || value.isNegative()) {
        throw new IllegalArgumentException(name + " must be positive");
      }
    }

    private static String normalise(String value) {
      if (value == null || value.isBlank()) {
        throw new IllegalArgumentException("baseUrl is required");
      }
      URI uri = URI.create(value.strip());
      String scheme = String.valueOf(uri.getScheme()).toLowerCase(Locale.ROOT);
      boolean web = "http".equals(scheme) || "https".equals(scheme);
      boolean bare =
          uri.getRawQuery() == null && uri.getRawFragment() == null && uri.getRawUserInfo() == null;
      if (!web || !bare || uri.getHost() == null) {
        throw new IllegalArgumentException("baseUrl must be an http(s) URL with a host");
      }
      String text = uri.toString();
      return text.endsWith("/") ? text.substring(0, text.length() - 1) : text;
    }

    /**
     * Names the settings; the credentials are redacted.
     *
     * @return a description safe to log
     */
    @Override
    public String toString() {
      return new Redacted("CryptoServiceClient.Builder")
          .show("baseUrl", baseUrl)
          .hide("apiKey", apiKey)
          .hide("token", token)
          .show("timeout", timeout)
          .show("deadline", deadline)
          .show("retry", retry)
          .toString();
    }
  }
}
