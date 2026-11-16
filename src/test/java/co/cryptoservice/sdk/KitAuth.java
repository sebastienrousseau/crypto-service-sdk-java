// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The live server the kit runs against, and one client per credential a vector asks for.
 *
 * <p>{@code CRYPTO_SERVICE_URL}, {@code CRYPTO_SERVICE_API_KEY} and {@code
 * CRYPTO_SERVICE_JWT_SECRET} are required by {@code make conformance}, which fails without them; a
 * plain {@code mvn verify} skips the kit. With {@code
 * CRYPTO_SERVICE_JWT_SECRET} (the server's {@code JWT_SECRET}) the runner mints HS256 tokens the way
 * the reference runner does: each file runs as its own principal {@code conformance-<group>}, which
 * keeps every file under the server's 60-requests-a-minute tenant quota, and the vectors that ask
 * for specific scopes can run. Without it, admin vectors use the API key and scoped vectors fail.
 * Token minting ({@link TestTokens}) is test tooling; the SDK itself never signs anything.
 */
final class KitAuth {

  /** Waits for a tenant quota reset (Retry-After up to 65 s) instead of failing a vector. */
  private static final RetryOptions PACING =
      new RetryOptions(2, Duration.ofMillis(200), Duration.ofSeconds(65));

  private final String url;
  private final String apiKey;
  private final String jwtSecret;
  private final String group;
  private final Map<String, CryptoServiceClient> clients = new HashMap<>();
  private final List<ResponseInfo> responses = new ArrayList<>();

  private KitAuth(String url, String apiKey, String jwtSecret, String group) {
    this.url = url;
    this.apiKey = apiKey;
    this.jwtSecret = jwtSecret;
    this.group = group;
  }

  /** The live server from the environment, for one kit file; skips or fails when it is unset. */
  static KitAuth fromEnvironment(String group) {
    String url = System.getenv("CRYPTO_SERVICE_URL");
    String key = System.getenv("CRYPTO_SERVICE_API_KEY");
    boolean configured = url != null && !url.isBlank() && key != null && !key.isBlank();
    if (!configured && Boolean.getBoolean("conformance.required")) {
      fail("CRYPTO_SERVICE_URL and CRYPTO_SERVICE_API_KEY must be set to run the conformance kit");
    }
    assumeTrue(configured, "CRYPTO_SERVICE_URL is not set: the conformance kit needs a live server");
    String secret = System.getenv("CRYPTO_SERVICE_JWT_SECRET");
    boolean jwt = secret != null && !secret.isBlank();
    if (!jwt && Boolean.getBoolean("conformance.required")) {
      fail("CRYPTO_SERVICE_JWT_SECRET must be set to the server's JWT_SECRET to run every vector");
    }
    return new KitAuth(url, key, jwt ? secret : null, group);
  }

  /** The last response any of this file's clients received. */
  ResponseInfo lastResponse() {
    return responses.get(responses.size() - 1);
  }

  /** The client for a vector's {@code auth}: absent or "admin", "none", or {"scopes": [...]}. */
  CryptoServiceClient client(JsonNode auth) {
    String name = auth == null ? "admin" : auth.isTextual() ? auth.asText() : auth.toString();
    return clients.computeIfAbsent(name, n -> build(auth));
  }

  private CryptoServiceClient build(JsonNode auth) {
    CryptoServiceClient.Builder b =
        CryptoServiceClient.builder()
            .baseUrl(url)
            .timeout(Duration.ofSeconds(60))
            .retry(PACING)
            .responseListener(this::record);
    if (auth != null && auth.isTextual() && auth.asText().equals("none")) {
      return b.build();
    }
    boolean admin = auth == null || auth.isTextual();
    if (jwtSecret == null) {
      if (!admin) {
        fail("this vector needs specific scopes: set CRYPTO_SERVICE_JWT_SECRET to the server's JWT_SECRET");
      }
      return b.apiKey(apiKey).build();
    }
    List<String> scopes = new ArrayList<>();
    (admin ? Json.MAPPER.createArrayNode().add("crypto:admin") : auth.path("scopes"))
        .forEach(s -> scopes.add(s.asText()));
    return b.token(TestTokens.mint(jwtSecret, "conformance-" + group, scopes)).build();
  }

  private synchronized void record(ResponseInfo info) {
    responses.add(info);
  }

  /** Closes every client. */
  void close() {
    clients.values().forEach(CryptoServiceClient::close);
  }
}
