// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * HS256 tokens for a crypto-server started with {@code JWT_SECRET}, as the server's reference
 * conformance runner mints them. Test and benchmark tooling only, with the JDK's HMAC: the SDK
 * itself signs nothing.
 */
final class TestTokens {

  private TestTokens() {}

  /** A token for {@code sub} with exactly {@code scopes}, valid for ten minutes. */
  static String mint(String secret, String sub, List<String> scopes) {
    long now = System.currentTimeMillis() / 1000;
    ObjectNode claims = Json.MAPPER.createObjectNode().put("sub", sub);
    scopes.forEach(claims.putArray("scopes")::add);
    claims.put("iat", now).put("exp", now + 600);
    optional(claims, "iss", "CRYPTO_SERVICE_JWT_ISSUER");
    optional(claims, "aud", "CRYPTO_SERVICE_JWT_AUDIENCE");
    String input =
        b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8))
            + "."
            + b64(claims.toString().getBytes(StandardCharsets.UTF_8));
    return input + "." + b64(hmac(secret, input));
  }

  private static void optional(ObjectNode claims, String claim, String variable) {
    String value = System.getenv(variable);
    if (value != null && !value.isBlank()) {
      claims.put(claim, value);
    }
  }

  private static String b64(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  private static byte[] hmac(String secret, String input) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException(e);
    }
  }
}
