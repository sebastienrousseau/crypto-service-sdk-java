// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.cryptoservice.sdk.TestServer.Answer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.http.HttpTimeoutException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/** The retry-safety table, backoff, Retry-After, timeouts, the deadline and cancellation. */
class RetryTest {

  private static final String HASH_OK =
      "{\"data\":{\"digest\":\"00\",\"algorithm\":\"sha256\",\"length\":1}}";
  private static final String KEY_OK =
      "{\"data\":{\"keyId\":\"k_AAAAAAAAAAAAAAAAAAAAAA\",\"algorithm\":\"ed25519\","
          + "\"publicKey\":\"00\",\"kid\":\"x\",\"metadata\":{\"kid\":\"x\"}}}";
  private static final RetryOptions FAST =
      new RetryOptions(3, Duration.ofMillis(1), Duration.ofMillis(20));
  private static final Clock EPOCH = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);

  private static HashRequest hash() {
    return new HashRequest(HashAlgorithm.SHA256, "abc");
  }

  @Test
  void retryOptionsAreChecked() {
    assertThrows(IllegalArgumentException.class, () -> RetryOptions.of(-1));
    assertThrows(
        IllegalArgumentException.class, () -> new RetryOptions(1, null, Duration.ofSeconds(1)));
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryOptions(1, Duration.ZERO, Duration.ofSeconds(1)));
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryOptions(1, Duration.ofSeconds(1), Duration.ofSeconds(-1)));
    assertThrows(
        IllegalArgumentException.class,
        () -> new RetryOptions(1, Duration.ofSeconds(2), Duration.ofSeconds(1)));
    assertEquals(0, RetryOptions.NONE.maxRetries());
    assertEquals(Duration.ofMillis(200), RetryOptions.of(2).initialDelay());
    assertEquals(Duration.ofSeconds(2), RetryOptions.of(2).maxDelay());
  }

  @Test
  void retrySafetyTableMatchesTheTypeScriptSdk() {
    assertTrue(Route.isIdempotent("GET", "/v2/keys/generate"));
    assertTrue(Route.isIdempotent("POST", "/v2/hash"));
    assertTrue(Route.isIdempotent("POST", "/v2/opaque/login/init"));
    for (String path :
        new String[] {
          "/v2/keys/generate",
          "/v2/keys/delete",
          "/v2/pq/keygen",
          "/v2/pq/dsa/keygen",
          "/v2/pq/slh-dsa/keygen",
          "/v2/pq/hybrid/keygen",
          "/v2/hpke/keygen",
          "/v2/kms/create-key",
          "/v2/opaque/register/init",
          "/v2/opaque/register/finish"
        }) {
      assertFalse(Route.isIdempotent("POST", path), path);
    }
  }

  @Test
  void fullJitterIsBelowTheExponentialCap() {
    RetryOptions options = new RetryOptions(40, Duration.ofMillis(100), Duration.ofSeconds(1));
    Backoff top = new Backoff(options, () -> 0.999999, EPOCH);
    assertTrue(top.jitter(1).compareTo(Duration.ofMillis(100)) < 0);
    assertTrue(top.jitter(2).compareTo(Duration.ofMillis(199)) > 0);
    assertTrue(top.jitter(5).compareTo(Duration.ofSeconds(1)) < 0);
    assertTrue(top.jitter(5).compareTo(Duration.ofMillis(999)) > 0);
    assertTrue(top.jitter(40).compareTo(Duration.ofSeconds(1)) < 0);
    Backoff bottom = new Backoff(options, () -> 0.0, EPOCH);
    assertEquals(Duration.ZERO, bottom.jitter(3));
    Backoff huge =
        new Backoff(
            new RetryOptions(64, Duration.ofDays(365), Duration.ofDays(3650)), () -> 0.5, EPOCH);
    assertEquals(Duration.ofDays(1825), huge.jitter(31));
  }

  @Test
  void decisionsAfterAStatus() {
    Backoff backoff =
        new Backoff(
            new RetryOptions(2, Duration.ofMillis(100), Duration.ofSeconds(2)), () -> 0.5, EPOCH);
    Optional<String> none = Optional.empty();
    assertEquals(Optional.of(Duration.ofMillis(50)), backoff.afterStatus(1, 503, true, none));
    assertEquals(Optional.of(Duration.ofMillis(50)), backoff.afterStatus(1, 429, false, none));
    assertEquals(Optional.of(Duration.ofMillis(100)), backoff.afterStatus(2, 504, true, none));
    assertEquals(Optional.empty(), backoff.afterStatus(1, 504, false, none));
    assertEquals(Optional.empty(), backoff.afterStatus(3, 503, true, none));
    assertEquals(Optional.empty(), backoff.afterStatus(1, 500, true, none));
    assertEquals(Optional.empty(), backoff.afterStatus(1, 200, true, none));
    assertEquals(
        Optional.of(Duration.ofSeconds(1)), backoff.afterStatus(1, 429, true, Optional.of("1")));
    assertEquals(Optional.empty(), backoff.afterStatus(1, 429, true, Optional.of("3")));
    assertEquals(
        Optional.of(Duration.ofMillis(50)), backoff.afterStatus(1, 429, true, Optional.of("soon")));
  }

  @Test
  void decisionsAfterAFailure() {
    Backoff backoff =
        new Backoff(
            new RetryOptions(1, Duration.ofMillis(100), Duration.ofSeconds(2)), () -> 0.5, EPOCH);
    assertEquals(Optional.of(Duration.ofMillis(50)), backoff.afterFailure(1, true));
    assertEquals(Optional.empty(), backoff.afterFailure(1, false));
    assertEquals(Optional.empty(), backoff.afterFailure(2, true));
  }

  @Test
  void retryAfterSecondsAndDates() {
    Backoff backoff = new Backoff(RetryOptions.of(1), () -> 0.5, EPOCH);
    assertEquals(Optional.of(Duration.ofSeconds(7)), backoff.parseRetryAfter(" 7 "));
    assertEquals(Optional.of(Duration.ofMillis(1500)), backoff.parseRetryAfter("1.5"));
    assertEquals(
        Optional.of(Duration.ofSeconds(60)),
        backoff.parseRetryAfter("Thu, 1 Jan 1970 00:01:00 GMT"));
    Backoff later =
        new Backoff(
            RetryOptions.of(1),
            () -> 0.5,
            Clock.fixed(Instant.ofEpochSecond(3600), ZoneOffset.UTC));
    assertEquals(Optional.of(Duration.ZERO), later.parseRetryAfter("Thu, 1 Jan 1970 00:01:00 GMT"));
    assertEquals(Optional.empty(), backoff.parseRetryAfter("-1"));
    assertEquals(Optional.empty(), backoff.parseRetryAfter(""));
  }

  @Test
  void retriesATransientStatusThenSucceeds() throws IOException {
    try (TestServer server =
            TestServer.script(
                Answer.json(503, "{}"),
                Answer.json(429, "{}").with("Retry-After", "0"),
                Answer.json(200, HASH_OK));
        CryptoServiceClient client = server.client().retry(FAST).build()) {
      assertEquals("00", client.hash(hash()).digest());
      assertEquals(3, server.seen().size());
    }
  }

  @Test
  void givesUpWhenRetryAfterIsTooLong() throws IOException {
    String problem =
        "{\"type\":\"urn:crypto-service:problem:rate-limited\",\"title\":\"Too Many Requests\","
            + "\"status\":429,\"detail\":\"Tenant rate limit exceeded.\",\"limit\":60,\"resetSeconds\":30}";
    try (TestServer server =
            TestServer.script(Answer.json(429, problem).with("Retry-After", "30"));
        CryptoServiceClient client = server.client().retry(FAST).build()) {
      ApiError e = assertThrows(ApiError.class, () -> client.hash(hash()));
      assertEquals(429, e.status());
      assertEquals(60, e.problem().extensions().get("limit").asInt());
      assertEquals(1, server.seen().size());
    }
  }

  @Test
  void statefulRouteIsNotRetriedAfter504() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(504, "{}"), Answer.json(200, KEY_OK));
        CryptoServiceClient client = server.client().retry(FAST).build()) {
      ApiError e = assertThrows(ApiError.class, client::generateKeyPair);
      assertEquals(504, e.status());
      assertEquals(1, server.seen().size());
    }
  }

  @Test
  void statefulRouteIsRetriedAfter503() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(503, "{}"), Answer.json(200, KEY_OK));
        CryptoServiceClient client = server.client().retry(FAST).build()) {
      assertEquals("ed25519", client.generateKeyPair().algorithm());
      assertEquals(2, server.seen().size());
    }
  }

  @Test
  void stopsAfterMaxRetries() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(503, "{}"));
        CryptoServiceClient client = server.client().retry(FAST).build()) {
      assertEquals(503, assertThrows(ApiError.class, () -> client.hash(hash())).status());
      assertEquals(4, server.seen().size());
    }
  }

  @Test
  void connectionFailureIsRetriedOnlyWhenIdempotent() throws IOException {
    int port;
    try (ServerSocket socket = new ServerSocket(0)) {
      port = socket.getLocalPort();
    }
    try (CryptoServiceClient client =
        CryptoServiceClient.builder().baseUrl("http://127.0.0.1:" + port).retry(FAST).build()) {
      TransportError idempotent = assertThrows(TransportError.class, () -> client.hash(hash()));
      assertFalse(idempotent.timedOut());
      assertInstanceOf(IOException.class, idempotent.getCause());
      assertEquals("POST", idempotent.method());
      assertEquals("/v2/hash", idempotent.path());
      TransportError stateful = assertThrows(TransportError.class, client::generateKeyPair);
      assertTrue(stateful.getMessage().contains("/v2/keys/generate"));
    }
  }

  @Test
  void attemptTimeoutIsATimedOutTransportError() throws IOException {
    try (TestServer server =
            TestServer.script(Answer.json(200, HASH_OK).after(Duration.ofSeconds(2)));
        CryptoServiceClient client = server.client().timeout(Duration.ofMillis(200)).build()) {
      TransportError e = assertThrows(TransportError.class, () -> client.hash(hash()));
      assertTrue(e.timedOut());
      assertInstanceOf(HttpTimeoutException.class, e.getCause());
    }
  }

  @Test
  void deadlineBoundsTheWholeCall() throws IOException {
    try (TestServer server =
            TestServer.script(Answer.json(200, HASH_OK).after(Duration.ofSeconds(2)));
        CryptoServiceClient client = server.client().deadline(Duration.ofMillis(200)).build()) {
      TransportError e = assertThrows(TransportError.class, () -> client.hash(hash()));
      assertTrue(e.timedOut());
      assertTrue(e.getMessage().startsWith("deadline exceeded"), e.getMessage());
    }
  }

  @Test
  void deadlineStopsRetriesThatWouldOutliveIt() throws IOException {
    RetryOptions slow = new RetryOptions(5, Duration.ofSeconds(5), Duration.ofSeconds(10));
    try (TestServer server = TestServer.script(Answer.json(503, "{}").with("Retry-After", "5"));
        CryptoServiceClient client =
            server.client().retry(slow).deadline(Duration.ofSeconds(1)).build()) {
      assertEquals(503, assertThrows(ApiError.class, () -> client.hash(hash())).status());
      assertEquals(1, server.seen().size());
    }
  }

  @Test
  void deadlineThatDoesNotFireLeavesTheCallAlone() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client = server.client().deadline(Duration.ofSeconds(30)).build()) {
      assertEquals("00", client.hash(hash()).digest());
    }
  }

  @Test
  void cancellingTheFutureAbortsTheExchange() throws Exception {
    try (TestServer server =
            TestServer.script(Answer.json(200, HASH_OK).after(Duration.ofMillis(500)));
        CryptoServiceClient client = server.client().retry(FAST).build()) {
      CompletableFuture<HashResult> future = client.hashAsync(hash());
      while (server.seen().isEmpty()) {
        Thread.sleep(5);
      }
      assertTrue(future.cancel(true));
      assertThrows(CancellationException.class, future::join);
      Thread.sleep(700);
      assertEquals(1, server.seen().size());
    }
  }

  @Test
  void interruptingABlockingCallCancelsIt() throws Exception {
    try (TestServer server =
            TestServer.script(Answer.json(200, HASH_OK).after(Duration.ofSeconds(2)));
        CryptoServiceClient client = server.client().build()) {
      CompletableFuture<Throwable> thrown = new CompletableFuture<>();
      AtomicBoolean flagKept = new AtomicBoolean();
      Thread caller =
          new Thread(
              () -> {
                try {
                  client.hash(hash());
                  thrown.complete(null);
                } catch (RuntimeException e) {
                  flagKept.set(Thread.currentThread().isInterrupted());
                  thrown.complete(e);
                }
              });
      caller.start();
      while (server.seen().isEmpty()) {
        Thread.sleep(5);
      }
      caller.interrupt();
      caller.join(5000);
      TransportError e = assertInstanceOf(TransportError.class, thrown.join());
      assertFalse(e.timedOut());
      assertInstanceOf(InterruptedException.class, e.getCause());
      assertTrue(flagKept.get());
    }
  }
}
