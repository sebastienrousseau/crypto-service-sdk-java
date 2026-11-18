// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.time.Duration;

/**
 * Automatic retries of transient failures; off by default ({@code maxRetries = 0}).
 *
 * <p>With retries on, the client sends a call again after HTTP 429, 503 or 504, or after a lost
 * connection or an attempt timeout, up to {@link #maxRetries() maxRetries()} more times. Routes
 * that create or delete server state (key generation, {@code /v2/keys/delete}, {@code
 * /v2/kms/create-key}, {@code /v2/opaque/register/*}) are retried only after 429 or 503, when the
 * server certainly did not act; never after a 504 or a lost connection.
 *
 * <p>The wait before retry {@code n} (from 1) is full jitter: a random point in {@code [0,
 * min(maxDelay, initialDelay * 2^(n-1)))}. A {@code Retry-After} header (seconds or an HTTP date)
 * replaces it; when it asks for longer than {@link #maxDelay() maxDelay()} the client stops and
 * raises that answer rather than retry early.
 *
 * <pre>{@code
 * CryptoServiceClient client = CryptoServiceClient.builder()
 *     .baseUrl("http://127.0.0.1:3101")
 *     .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
 *     .retry(RetryOptions.of(3))
 *     .build();
 * }</pre>
 *
 * @param maxRetries the retries after the first attempt (0 or more)
 * @param initialDelay the cap on the first retry's wait (positive)
 * @param maxDelay the cap on any wait, and the longest {@code Retry-After} honoured (no shorter
 *     than {@code initialDelay})
 */
public record RetryOptions(int maxRetries, Duration initialDelay, Duration maxDelay) {

  /** The default initial delay, 200 ms. */
  public static final Duration DEFAULT_INITIAL_DELAY = Duration.ofMillis(200);

  /** The default maximum delay, 2 s. */
  public static final Duration DEFAULT_MAX_DELAY = Duration.ofSeconds(2);

  /** No retries: every call is sent once. */
  public static final RetryOptions NONE = of(0);

  /**
   * Checks the values.
   *
   * @throws IllegalArgumentException when {@code maxRetries} is negative, a delay is missing or not
   *     positive, or {@code maxDelay} is shorter than {@code initialDelay}
   */
  public RetryOptions {
    if (maxRetries < 0) {
      throw new IllegalArgumentException("maxRetries must not be negative");
    }
    if (!positive(initialDelay) || !positive(maxDelay)) {
      throw new IllegalArgumentException("retry delays must be positive");
    }
    if (maxDelay.compareTo(initialDelay) < 0) {
      throw new IllegalArgumentException("maxDelay must not be shorter than initialDelay");
    }
  }

  /**
   * {@code maxRetries} retries with the default delays (200 ms initial, 2 s maximum).
   *
   * @param maxRetries the retries after the first attempt
   * @return the options
   */
  public static RetryOptions of(int maxRetries) {
    return new RetryOptions(maxRetries, DEFAULT_INITIAL_DELAY, DEFAULT_MAX_DELAY);
  }

  private static boolean positive(Duration value) {
    return value != null && !value.isZero() && !value.isNegative();
  }
}
