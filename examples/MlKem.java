// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau

import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.KemDecapsulateResult;
import co.cryptoservice.sdk.KeyIdRequest;
import co.cryptoservice.sdk.MlKemDecapsulateRequest;
import co.cryptoservice.sdk.MlKemEncapsulateRequest;
import co.cryptoservice.sdk.MlKemEncapsulateResult;
import co.cryptoservice.sdk.MlKemKeyPair;
import co.cryptoservice.sdk.RetryOptions;

/**
 * ML-KEM-768 (FIPS 203) with a server-held key: encapsulate a shared secret to its public key,
 * decapsulate it by keyId, and check both sides agree.
 */
public final class MlKem {

  private MlKem() {}

  public static void main(String[] args) {
    try (CryptoServiceClient client =
        CryptoServiceClient.builder()
            .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
            .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
            .retry(RetryOptions.of(3))
            .build()) {
      MlKemKeyPair pair = client.mlKemGenerateKeyPair();
      System.out.println(pair.algorithm() + " key " + pair.keyId());
      MlKemEncapsulateResult sent =
          client.mlKemEncapsulate(new MlKemEncapsulateRequest(pair.publicKey()));
      KemDecapsulateResult received =
          client.mlKemDecapsulate(new MlKemDecapsulateRequest(pair.keyId(), sent.ciphertext()));
      System.out.println("ciphertext: " + sent.ciphertextBytes().length + " bytes");
      boolean agree = sent.sharedSecret().equals(received.sharedSecret());
      System.out.println("shared secrets agree: " + agree);
      client.deleteKey(new KeyIdRequest(pair.keyId()));
      if (!agree) {
        throw new IllegalStateException("shared secrets differ");
      }
    }
  }
}
