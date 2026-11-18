// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau

import co.cryptoservice.sdk.AeadResult;
import co.cryptoservice.sdk.Bytes;
import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.DecryptRequest;
import co.cryptoservice.sdk.DecryptResult;
import co.cryptoservice.sdk.EncryptRequest;
import co.cryptoservice.sdk.RetryOptions;

/**
 * XChaCha20-Poly1305 encryption and decryption, both done by the server.
 *
 * <p>The key is a synthetic placeholder (bytes 0x00 to 0x1f) sent as hex, as the API expects; the
 * ciphertext comes back as base64 (nonce, ciphertext and tag). Never hard-code a real key.
 */
public final class AeadRoundTrip {

  private AeadRoundTrip() {}

  public static void main(String[] args) {
    byte[] key = new byte[32];
    for (int i = 0; i < key.length; i++) {
      key[i] = (byte) i;
    }
    try (CryptoServiceClient client =
        CryptoServiceClient.builder()
            .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
            .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
            .retry(RetryOptions.of(3))
            .build()) {
      AeadResult sealed = client.encrypt(new EncryptRequest(Bytes.hex(key), "round trip"));
      System.out.println(sealed.algorithm() + ", " + sealed.ciphertextBytes().length + " bytes");
      DecryptResult opened =
          client.decrypt(new DecryptRequest(Bytes.hex(key), sealed.ciphertext()));
      System.out.println("decrypted: " + opened.plaintext());
      if (!"round trip".equals(opened.plaintext())) {
        throw new IllegalStateException("round trip failed");
      }
    }
  }
}
