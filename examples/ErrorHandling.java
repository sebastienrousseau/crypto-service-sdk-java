// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau

import co.cryptoservice.sdk.ApiError;
import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.DecryptRequest;
import co.cryptoservice.sdk.Problem;
import co.cryptoservice.sdk.RetryOptions;
import co.cryptoservice.sdk.SignRequest;
import co.cryptoservice.sdk.TransportError;
import co.cryptoservice.sdk.ValidationError;
import java.time.Duration;

/**
 * The two failures a call can raise: {@link ApiError}, with the RFC 9457 problem, when the server
 * answers outside 2xx; {@link TransportError} when no usable answer arrives. Shown with a wrong
 * AEAD key (400, code AUTH_FAILED), an unknown key (404), a malformed key id (400 with errors[]),
 * and a port where nothing listens.
 */
public final class ErrorHandling {

  private ErrorHandling() {}

  public static void main(String[] args) {
    String baseUrl = System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101");
    try (CryptoServiceClient client =
        CryptoServiceClient.builder()
            .baseUrl(baseUrl)
            .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
            .retry(RetryOptions.of(3))
            .build()) {
      String wrongKey = "ff".repeat(32);
      String ciphertext = "QEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXt1ZrFr+SFHfhl+I6efFIvDs4l+LBF6yHvvIs";
      expect(() -> client.decrypt(new DecryptRequest(wrongKey, ciphertext)), "AUTH_FAILED");
      expect(() -> client.sign(new SignRequest("k_" + "A".repeat(22), "m")), "KEY_NOT_FOUND");
      expect(() -> client.sign(new SignRequest("not-a-key-id", "m")), null);
    }
    try (CryptoServiceClient nowhere =
        CryptoServiceClient.builder()
            .baseUrl("http://127.0.0.1:9")
            .timeout(Duration.ofSeconds(2))
            .build()) {
      nowhere.health();
      throw new IllegalStateException("expected a TransportError");
    } catch (TransportError e) {
      System.out.println("TransportError: " + e.getMessage() + ", timedOut=" + e.timedOut());
    }
  }

  private static void expect(Runnable call, String code) {
    try {
      call.run();
      throw new IllegalStateException("expected an ApiError");
    } catch (ApiError e) {
      Problem p = e.problem();
      System.out.println(e.status() + " " + p.type() + " code=" + p.code() + ": " + p.detail());
      for (ValidationError v : p.errors()) {
        System.out.println("  field " + v.field() + ": " + v.message());
      }
      if (code != null && !code.equals(p.code())) {
        throw new IllegalStateException("expected code " + code, e);
      }
    }
  }
}
