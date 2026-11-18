// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.regex.Pattern;

/**
 * When to retry, and after how long: {@link RetryOptions} applied to one attempt's outcome. The
 * decisions are those of the TypeScript SDK's {@code request.ts}.
 */
final class Backoff {

  private static final Pattern SECONDS = Pattern.compile("[0-9]{1,9}(\\.[0-9]{1,9})?");

  private final RetryOptions options;
  private final DoubleSupplier random;
  private final Clock clock;

  Backoff(RetryOptions options) {
    this(options, () -> ThreadLocalRandom.current().nextDouble(), Clock.systemUTC());
  }

  Backoff(RetryOptions options, DoubleSupplier random, Clock clock) {
    this.options = options;
    this.random = random;
    this.clock = clock;
  }

  /** Whether a status is worth another attempt at all. */
  static boolean isTransient(int status) {
    return status == 429 || status == 503 || status == 504;
  }

  /**
   * The wait before the next attempt after attempt {@code attempt} (from 1) got no response, or
   * empty to give up. Only an idempotent route is retried then.
   */
  Optional<Duration> afterFailure(int attempt, boolean idempotent) {
    if (!idempotent || attempt > options.maxRetries()) {
      return Optional.empty();
    }
    return Optional.of(jitter(attempt));
  }

  /**
   * The wait before the next attempt after attempt {@code attempt} answered {@code status}, or
   * empty to give up. A 504 may have been processed upstream, so a route that is not idempotent is
   * not retried after one; a 429 or 503 was not processed.
   */
  Optional<Duration> afterStatus(
      int attempt, int status, boolean idempotent, Optional<String> retryAfter) {
    if (attempt > options.maxRetries() || !isTransient(status)) {
      return Optional.empty();
    }
    if (!idempotent && status == 504) {
      return Optional.empty();
    }
    Optional<Duration> asked = retryAfter.flatMap(this::parseRetryAfter);
    if (asked.isPresent()) {
      return asked.filter(d -> d.compareTo(options.maxDelay()) <= 0);
    }
    return Optional.of(jitter(attempt));
  }

  /** Full jitter: a random point below {@code min(maxDelay, initialDelay * 2^(attempt-1))}. */
  Duration jitter(int attempt) {
    int doublings = Math.min(attempt - 1, 30);
    long initial = options.initialDelay().toNanos();
    long cap = options.maxDelay().toNanos();
    long base = initial > (cap >> doublings) ? cap : Math.min(cap, initial << doublings);
    return Duration.ofNanos((long) (base * random.getAsDouble()));
  }

  /** {@code Retry-After} as (possibly fractional) seconds or an HTTP date; empty otherwise. */
  Optional<Duration> parseRetryAfter(String value) {
    String text = value.strip();
    try {
      if (SECONDS.matcher(text).matches()) {
        return Optional.of(Duration.ofNanos((long) (Double.parseDouble(text) * 1e9)));
      }
      Instant at = ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
      Duration wait = Duration.between(clock.instant(), at);
      return Optional.of(wait.isNegative() ? Duration.ZERO : wait);
    } catch (NumberFormatException | DateTimeParseException e) {
      return Optional.empty();
    }
  }
}
