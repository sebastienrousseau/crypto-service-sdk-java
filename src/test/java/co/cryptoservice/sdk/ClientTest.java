// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.cryptoservice.sdk.TestServer.Answer;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;

/** The builder, the headers every request carries, the raw request and closing. */
class ClientTest {

  private static final String HASH_OK =
      "{\"data\":{\"digest\":\"ba7816bf\",\"algorithm\":\"sha256\",\"length\":4}}";

  @Test
  void sendsCredentialsUserAgentAndJson() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client = server.client().token("jwt-token").build()) {
      HashResult result = client.hash(new HashRequest(HashAlgorithm.SHA256, "abc"));
      assertEquals("ba7816bf", result.digest());
      assertArrayEquals(new byte[] {(byte) 0xba, 0x78, 0x16, (byte) 0xbf}, result.digestBytes());
      TestServer.Seen seen = server.seen().get(0);
      assertEquals("POST", seen.method());
      assertEquals("/v2/hash", seen.path());
      assertEquals("test-api-key", seen.header("x-api-key"));
      assertEquals("Bearer jwt-token", seen.header("Authorization"));
      assertEquals(
          "crypto-service-sdk-java/" + CryptoServiceClient.VERSION, seen.header("User-Agent"));
      assertEquals("application/json", seen.header("Content-Type"));
      assertEquals("{\"algorithm\":\"sha256\",\"data\":\"abc\"}", seen.text());
      assertNull(seen.header("traceparent"));
    }
  }

  @Test
  void publicRoutesCarryNoCredentialsAndGetsNoBody() throws IOException {
    String health = "{\"statusCode\":200,\"status\":\"ok\",\"uptime\":1.5}";
    try (TestServer server = TestServer.script(Answer.json(200, health));
        CryptoServiceClient client = server.client().token("t").build()) {
      HealthResult result = client.health();
      assertEquals("ok", result.status());
      TestServer.Seen seen = server.seen().get(0);
      assertEquals("GET", seen.method());
      assertNull(seen.header("x-api-key"));
      assertNull(seen.header("Authorization"));
      assertNull(seen.header("Content-Type"));
      assertEquals(0, seen.body().length);
    }
  }

  @Test
  void noCredentialsWhenNoneConfigured() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client = CryptoServiceClient.builder().baseUrl(server.url()).build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "abc"));
      assertNull(server.seen().get(0).header("x-api-key"));
      assertNull(server.seen().get(0).header("Authorization"));
    }
  }

  @Test
  void fixedTraceparent() throws IOException {
    String tp = "00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01";
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client = server.client().traceparent(" " + tp + " ").build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "abc"));
      assertEquals(tp, server.seen().get(0).header("traceparent"));
    }
  }

  @Test
  void generatedTraceparentPerRequest() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client = server.client().autoTraceparent(true).build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      client.hash(new HashRequest(HashAlgorithm.SHA256, "b"));
      String first = server.seen().get(0).header("traceparent");
      String second = server.seen().get(1).header("traceparent");
      assertTrue(first.matches("00-[0-9a-f]{32}-[0-9a-f]{16}-01"), first);
      assertFalse(first.equals(second));
    }
  }

  @Test
  void traceparentCanBeTurnedOff() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client =
            server
                .client()
                .autoTraceparent(true)
                .autoTraceparent(false)
                .traceparent(null)
                .build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      assertNull(server.seen().get(0).header("traceparent"));
    }
  }

  @Test
  void traceparentIsValidated() {
    CryptoServiceClient.Builder builder = CryptoServiceClient.builder();
    assertThrows(IllegalArgumentException.class, () -> builder.traceparent("not-a-trace"));
  }

  @Test
  void generatedTraceIdsAreNeverZero() {
    java.util.Random zeros =
        new java.util.Random() {
          private static final long serialVersionUID = 1L;

          @Override
          public void nextBytes(byte[] bytes) {
            java.util.Arrays.fill(bytes, (byte) 0);
          }
        };
    assertEquals(
        "00-01000000000000000000000000000000-0100000000000000-01", Traceparent.generate(zeros));
  }

  @Test
  void rawRequestReturnsAnyStatus() throws IOException {
    Answer text =
        new Answer(
            418,
            java.util.Map.of("Content-Type", "text/plain; charset=utf-8"),
            "teapot",
            Duration.ZERO);
    try (TestServer server = TestServer.script(text);
        CryptoServiceClient client = server.client().build()) {
      RawResponse response =
          client.request(
              "post", "/v1/encrypt", "<a/>".getBytes(StandardCharsets.UTF_8), "application/xml");
      assertEquals(418, response.status());
      assertEquals(Optional.of("text/plain"), response.mediaType());
      assertEquals("teapot", response.bodyText());
      assertEquals("teapot", new String(response.body(), StandardCharsets.UTF_8));
      assertTrue(response.headers().firstValue("content-type").isPresent());
      assertEquals(
          "RawResponse[status=418, mediaType=text/plain, body=<redacted>]", response.toString());
      assertEquals("POST", server.seen().get(0).method());
      assertEquals("application/xml", server.seen().get(0).header("Content-Type"));
      assertEquals("test-api-key", server.seen().get(0).header("x-api-key"));
    }
  }

  @Test
  void rawRequestDefaultsToJsonAndAllowsNoBody() throws IOException {
    Answer empty = new Answer(204, java.util.Map.of(), "", Duration.ZERO);
    try (TestServer server = TestServer.script(empty);
        CryptoServiceClient client = server.client().build()) {
      RawResponse get = client.request("GET", "/metrics", null, null);
      assertEquals(204, get.status());
      assertEquals(Optional.empty(), get.mediaType());
      client.request("POST", "/v2/hash", new byte[] {'{', '}'}, null);
      assertEquals("application/json", server.seen().get(1).header("Content-Type"));
    }
  }

  @Test
  void rawRequestChecksItsArguments() {
    try (CryptoServiceClient client =
        CryptoServiceClient.builder().baseUrl("http://127.0.0.1:9").build()) {
      assertThrows(NullPointerException.class, () -> client.requestAsync(null, "/", null, null));
      assertThrows(
          IllegalArgumentException.class, () -> client.requestAsync("GET", null, null, null));
      assertThrows(
          IllegalArgumentException.class, () -> client.requestAsync("GET", "health", null, null));
    }
  }

  @Test
  void rawPathCannotChangeTheHost() {
    // The path is appended to the base URL, so whatever it holds the request
    // (and its credentials) still goes to the configured server.
    CryptoServiceClient.Builder builder =
        CryptoServiceClient.builder().baseUrl("https://api.example:8443/base").apiKey("k");
    builder.build().close();
    Transport transport = new Transport(builder, java.net.http.HttpClient.newHttpClient(), false);
    for (String path : List.of("//evil.example/x", "/@evil.example/y", "/..//evil.example:1/z")) {
      java.net.URI uri = transport.request(Route.of("GET", path, true), null, null).uri();
      assertEquals("api.example", uri.getHost(), path);
      assertEquals(8443, uri.getPort(), path);
      assertTrue(uri.getRawPath().startsWith("/base/"), path);
    }
  }

  @Test
  void typedPostNeedsARequest() {
    try (CryptoServiceClient client =
        CryptoServiceClient.builder().baseUrl("http://127.0.0.1:9").build()) {
      assertThrows(NullPointerException.class, () -> client.hashAsync(null));
    }
  }

  @Test
  void baseUrlIsNormalised() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client =
            CryptoServiceClient.builder().baseUrl(" " + server.url() + "/ ").build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      assertEquals("/v2/hash", server.seen().get(0).path());
    }
  }

  @Test
  void baseUrlKeepsAPathPrefix() throws IOException {
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client =
            CryptoServiceClient.builder().baseUrl(server.url() + "/crypto").build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      assertEquals("/crypto/v2/hash", server.seen().get(0).path());
    }
  }

  @Test
  void builderRejectsBadSettings() {
    for (String url :
        List.of(
            "",
            " ",
            "ftp://host",
            "http://host/?q=1",
            "http://host/#f",
            "http:///path",
            "https://user:pw@host",
            "mailto:x@y")) {
      CryptoServiceClient.Builder b = CryptoServiceClient.builder().baseUrl(url);
      assertThrows(IllegalArgumentException.class, b::build, url);
    }
    assertThrows(IllegalArgumentException.class, () -> CryptoServiceClient.builder().build());
    assertThrows(
        IllegalArgumentException.class,
        () -> CryptoServiceClient.builder().baseUrl("http://h").timeout(Duration.ZERO).build());
    assertThrows(
        IllegalArgumentException.class,
        () -> CryptoServiceClient.builder().baseUrl("http://h").timeout(null).build());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            CryptoServiceClient.builder()
                .baseUrl("http://h")
                .timeout(Duration.ofSeconds(-1))
                .build());
    assertThrows(
        IllegalArgumentException.class,
        () -> CryptoServiceClient.builder().baseUrl("http://h").deadline(Duration.ZERO).build());
    assertThrows(
        IllegalArgumentException.class,
        () -> CryptoServiceClient.builder().baseUrl("http://h").apiKey("a\r\nb").build());
    assertThrows(
        IllegalArgumentException.class,
        () -> CryptoServiceClient.builder().baseUrl("http://h").token("a\nb").build());
    assertThrows(NullPointerException.class, () -> CryptoServiceClient.builder().retry(null));
  }

  @Test
  void injectedHttpClientMustNotFollowRedirects() {
    HttpClient follows =
        HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    CryptoServiceClient.Builder b =
        CryptoServiceClient.builder().baseUrl("http://h").httpClient(follows);
    assertThrows(IllegalArgumentException.class, b::build);
  }

  @Test
  void injectedHttpClientIsUsedAndLeftOpen() throws IOException {
    HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK))) {
      CryptoServiceClient client = server.client().httpClient(http).build();
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      client.close();
      try (CryptoServiceClient again = server.client().httpClient(http).build()) {
        again.hash(new HashRequest(HashAlgorithm.SHA256, "b"));
      }
      assertEquals(2, server.seen().size());
    }
  }

  @Test
  void redirectsAreRefused() throws IOException {
    Answer redirect =
        new Answer(302, java.util.Map.of("Location", "http://example.invalid/"), "", Duration.ZERO);
    try (TestServer server = TestServer.script(redirect);
        CryptoServiceClient client = server.client().build()) {
      ApiError e =
          assertThrows(
              ApiError.class, () -> client.hash(new HashRequest(HashAlgorithm.SHA256, "a")));
      assertEquals(302, e.status());
      assertEquals(Problem.ABOUT_BLANK, e.problem().type());
      assertEquals(1, server.seen().size());
    }
  }

  @Test
  void closedClientRefusesCalls() {
    CryptoServiceClient client =
        CryptoServiceClient.builder().baseUrl("http://127.0.0.1:9").build();
    client.close();
    client.close();
    assertThrows(
        IllegalStateException.class,
        () -> client.hashAsync(new HashRequest(HashAlgorithm.SHA256, "a")));
  }

  @Test
  void closeIfCloseableClosesOnlyCloseables() {
    List<String> closed = new CopyOnWriteArrayList<>();
    Transport.closeIfCloseable((AutoCloseable) () -> closed.add("closed"));
    Transport.closeIfCloseable("not closeable");
    assertEquals(List.of("closed"), closed);
    assertThrows(
        IllegalStateException.class,
        () ->
            Transport.closeIfCloseable(
                (AutoCloseable)
                    () -> {
                      throw new IOException("boom");
                    }));
  }

  @Test
  void listenerSeesEveryResponseAndCannotBreakTheCall() throws IOException {
    List<ResponseInfo> infos = new CopyOnWriteArrayList<>();
    try (TestServer server = TestServer.script(Answer.json(200, HASH_OK));
        CryptoServiceClient client =
            server
                .client()
                .responseListener(
                    info -> {
                      infos.add(info);
                      throw new IllegalStateException("listener failure");
                    })
                .build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      assertEquals(1, infos.size());
      ResponseInfo info = infos.get(0);
      assertEquals("POST", info.method());
      assertEquals("/v2/hash", info.path());
      assertEquals(200, info.status());
      assertEquals(1, info.attempt());
      assertEquals(Optional.of("application/json"), info.mediaType());
    }
  }

  @Test
  void builderToStringRedactsCredentials() {
    String text =
        CryptoServiceClient.builder()
            .baseUrl("http://h")
            .apiKey("secret-key")
            .token("secret-token")
            .toString();
    assertFalse(text.contains("secret"), text);
    assertTrue(text.contains("apiKey=<redacted>"), text);
    assertTrue(text.contains("deadline=null"), text);
    assertTrue(CryptoServiceClient.builder().toString().contains("apiKey=null, token=null"));
  }

  @Test
  void quietListenerSeesRetriedAttempts() throws IOException {
    List<Integer> attempts = new CopyOnWriteArrayList<>();
    try (TestServer server = TestServer.script(Answer.json(503, "{}"), Answer.json(200, HASH_OK));
        CryptoServiceClient client =
            server
                .client()
                .retry(new RetryOptions(1, Duration.ofMillis(1), Duration.ofMillis(5)))
                .responseListener(info -> attempts.add(info.attempt()))
                .build()) {
      client.hash(new HashRequest(HashAlgorithm.SHA256, "a"));
      assertEquals(List.of(1, 2), attempts);
    }
  }
}
