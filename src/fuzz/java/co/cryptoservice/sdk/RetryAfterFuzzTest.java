// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Fuzzes the {@code Retry-After} parser and the retry decision built on it: whatever the header
 * holds, {@link Backoff#parseRetryAfter} returns a wait that is never negative, or nothing, and
 * never throws; {@link Backoff#afterStatus} never asks for a wait above {@code maxDelay}.
 *
 * <p>The regression test for commit 0986a9f, which moved the seconds parse inside the {@code try}
 * after CodeQL flagged a possible uncaught {@link NumberFormatException}. The seeds under {@code
 * RetryAfterFuzzTestInputs/} run in {@code mvn verify}; {@code make fuzz} fuzzes.
 */
class RetryAfterFuzzTest {

  private static final RetryOptions OPTIONS =
      new RetryOptions(3, Duration.ofMillis(100), Duration.ofSeconds(2));
  private static final Backoff BACKOFF =
      new Backoff(OPTIONS, () -> 0.5, Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));

  @FuzzTest(maxDuration = "60s")
  void anyRetryAfterIsAWaitOrNothing(FuzzedDataProvider data) {
    String header = data.consumeRemainingAsString();
    Optional<Duration> wait = BACKOFF.parseRetryAfter(header);
    assertNotNull(wait);
    wait.ifPresent(d -> assertFalse(d.isNegative(), header));
    Optional<Duration> decision = BACKOFF.afterStatus(1, 429, true, Optional.of(header));
    decision.ifPresent(d -> assertTrue(d.compareTo(OPTIONS.maxDelay()) <= 0, header));
  }
}
