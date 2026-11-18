// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.cryptoservice.sdk.TestServer.Answer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

/** RFC 9457 problems, {@link ApiError}, {@link TransportError} and how failures surface. */
class ErrorModelTest {

  private static Problem parse(String body, int status) {
    return Problem.parse(body.getBytes(StandardCharsets.UTF_8), status);
  }

  @Test
  void readsAValidationProblem() {
    Problem p =
        parse(
            "{\"type\":\"urn:crypto-service:problem:validation-failed\",\"title\":\"Validation failed\","
                + "\"status\":400,\"detail\":\"body must have required property 'data'\","
                + "\"instance\":\"/v2/hash\",\"errors\":[{\"field\":\"/data\",\"message\":\"required\"},"
                + "{\"field\":7}],\"traceId\":\"abc\"}",
            400);
    assertEquals("urn:crypto-service:problem:validation-failed", p.type());
    assertEquals("Validation failed", p.title());
    assertEquals(400, p.status());
    assertEquals("/v2/hash", p.instance());
    assertNull(p.code());
    assertEquals(
        List.of(new ValidationError("/data", "required"), new ValidationError("", "")), p.errors());
    assertEquals("abc", p.extensions().get("traceId").asText());
    assertEquals(
        "Problem[type=urn:crypto-service:problem:validation-failed, title=Validation failed,"
            + " status=400, detail=<redacted>, instance=/v2/hash, code=null, errors=2,"
            + " extensions=[traceId]]",
        p.toString());
  }

  @Test
  void readsACodeAndFillsGaps() {
    Problem p =
        parse("{\"detail\":\"Decryption failed\",\"code\":\"AUTH_FAILED\",\"status\":\"x\"}", 400);
    assertEquals(Problem.ABOUT_BLANK, p.type());
    assertEquals("HTTP error", p.title());
    assertEquals(400, p.status());
    assertEquals("AUTH_FAILED", p.code());
    assertEquals(List.of(), p.errors());
    assertEquals(Map.of(), p.extensions());
  }

  @Test
  void anythingElseBecomesAboutBlank() {
    for (String body :
        List.of(
            "", "<html>bad gateway</html>", "[1,2]", "{\"detail\":5}", "\"text\"", "null", "{")) {
      Problem p = parse(body, 502);
      assertEquals(Problem.ABOUT_BLANK, p.type(), body);
      assertEquals(502, p.status());
      assertEquals("The server answered 502 without a problem body", p.detail());
    }
  }

  @Test
  void problemIsImmutable() {
    Problem p = Problem.blank(500);
    assertThrows(
        UnsupportedOperationException.class, () -> p.errors().add(new ValidationError("a", "b")));
    assertThrows(
        NullPointerException.class,
        () -> new Problem("t", "t", 1, "d", null, null, null, Map.of()));
  }

  @Test
  void apiErrorCarriesTheProblemNotTheBody() throws IOException {
    String body =
        "{\"type\":\"urn:crypto-service:problem:invalid-input\",\"title\":\"Invalid input\","
            + "\"status\":400,\"detail\":\"Decryption failed: invalid input\",\"instance\":\"/v2/decrypt\","
            + "\"code\":\"AUTH_FAILED\"}";
    try (TestServer server = TestServer.script(Answer.json(400, body));
        CryptoServiceClient client = server.client().build()) {
      ApiError e =
          assertThrows(
              ApiError.class, () -> client.decrypt(new DecryptRequest("ff", "secret-ciphertext")));
      assertEquals(400, e.status());
      assertEquals("AUTH_FAILED", e.problem().code());
      assertEquals("POST", e.method());
      assertEquals("/v2/decrypt", e.path());
      assertEquals(
          "crypto-service answered HTTP 400 (urn:crypto-service:problem:invalid-input) to POST /v2/decrypt",
          e.getMessage());
      assertFalse(e.getMessage().contains("secret"));
    }
  }

  @Test
  void serverTextCannotForgeLogLines() {
    // A hostile server's problem members end up in the exception message and in
    // Problem.toString, which callers log: control characters and Unicode line
    // separators must come out escaped, never raw.
    String body =
        "{\"type\":\"urn:x\\r\\n2026-01-01 INFO forged\",\"title\":\"t\\u2028u\","
            + "\"instance\":\"/i\\u0085j\",\"code\":\"C\\u0000D\",\"detail\":\"d\"}";
    Problem p = parse(body, 400);
    assertEquals("urn:x\r\n2026-01-01 INFO forged", p.type());
    ApiError e = new ApiError(400, p, Route.of("POST", "/v2/hash", true));
    assertEquals(
        "crypto-service answered HTTP 400 (urn:x\\u000d\\u000a2026-01-01 INFO forged) to POST /v2/hash",
        e.getMessage());
    assertEquals(
        "Problem[type=urn:x\\u000d\\u000a2026-01-01 INFO forged, title=t\\u2028u, status=400,"
            + " detail=<redacted>, instance=/i\\u0085j, code=C\\u0000D, errors=0, extensions=[]]",
        p.toString());
    assertEquals("a\\u2029b\\u007f", Redacted.printable("a b\u007f"));
    assertEquals("plain text", Redacted.printable("plain text"));
  }

  @Test
  void asyncFailuresCompleteTheFutureExceptionally() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(404, "{\"detail\":\"no\"}"));
        CryptoServiceClient client = server.client().build()) {
      CompletableFuture<SignResult> f =
          client.signAsync(new SignRequest("k_AAAAAAAAAAAAAAAAAAAAAA", "m"));
      CompletionException e = assertThrows(CompletionException.class, f::join);
      assertEquals(404, assertInstanceOf(ApiError.class, e.getCause()).status());
    }
  }

  @Test
  void malformedSuccessBodyIsATransportErrorWithoutTheBody() throws IOException {
    for (String body :
        List.of(
            "<html>secret-page</html>", "{\"data\":null}", "{}", "{\"data\":{\"length\":\"x\"}}")) {
      try (TestServer server = TestServer.script(Answer.json(200, body));
          CryptoServiceClient client = server.client().build()) {
        TransportError e =
            assertThrows(
                TransportError.class,
                () -> client.hash(new HashRequest(HashAlgorithm.SHA256, "a")),
                body);
        assertFalse(e.timedOut());
        assertNull(e.getCause());
        assertEquals("malformed response body (POST /v2/hash)", e.getMessage());
      }
    }
  }

  @Test
  void transportErrorClassifiesTimeouts() {
    Route route = Route.of("GET", "/health", false);
    assertTrue(TransportError.of(route, new java.net.http.HttpTimeoutException("t")).timedOut());
    assertTrue(
        TransportError.of(route, new java.net.http.HttpConnectTimeoutException("t")).timedOut());
    TransportError network = TransportError.of(route, new java.net.ConnectException("refused"));
    assertFalse(network.timedOut());
    assertEquals("network error (GET /health)", network.getMessage());
  }

  @Test
  void unwrapAndRethrow() {
    IllegalStateException inner = new IllegalStateException("x");
    assertSame(inner, Transport.unwrap(new CompletionException(inner)));
    CompletionException bare = new CompletionException("no cause", null);
    assertSame(bare, Transport.unwrap(bare));
    assertSame(inner, Transport.unwrap(inner));
    assertSame(inner, ClientCore.rethrow(inner));
    AssertionError error = new AssertionError("e");
    assertSame(error, assertThrows(AssertionError.class, () -> ClientCore.rethrow(error)));
    Exception checked = new Exception("checked");
    assertSame(checked, ClientCore.rethrow(checked).getCause());
  }

  @Test
  void mapPassesFailuresThroughAndCancelsTheSource() {
    CompletableFuture<String> source = new CompletableFuture<>();
    CompletableFuture<Integer> mapped = ClientCore.map(source, String::length);
    source.completeExceptionally(new CompletionException(new IllegalStateException("x")));
    assertInstanceOf(
        IllegalStateException.class,
        assertThrows(CompletionException.class, mapped::join).getCause());
    CompletableFuture<String> pending = new CompletableFuture<>();
    ClientCore.map(pending, String::length).cancel(true);
    assertTrue(pending.isCancelled());
  }

  @Test
  void jsonEncodeRejectsUnwritableValues() {
    assertThrows(IllegalArgumentException.class, () -> Json.encode(new Object()));
  }

  @Test
  void blockingCallUnwrapsTheCause() throws IOException {
    try (TestServer server =
            TestServer.script(Answer.json(500, "{\"detail\":\"boom\"}").after(Duration.ZERO));
        CryptoServiceClient client = server.client().build()) {
      assertEquals(500, assertThrows(ApiError.class, client::algorithms).status());
    }
  }
}
