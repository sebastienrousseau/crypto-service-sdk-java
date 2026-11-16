// SPDX-License-Identifier: Apache-2.0 OR MIT

import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.KeyGenerateResult;
import co.cryptoservice.sdk.KeyIdRequest;
import co.cryptoservice.sdk.RetryOptions;
import co.cryptoservice.sdk.SignRequest;
import co.cryptoservice.sdk.SignResult;
import co.cryptoservice.sdk.VerifyRequest;
import co.cryptoservice.sdk.VerifyResult;

/**
 * Ed25519 with a server-held key: generate it, sign by keyId, verify with the public key, then
 * delete the key. The private key never leaves the server.
 */
public final class SignVerify {

  private SignVerify() {}

  public static void main(String[] args) {
    try (CryptoServiceClient client =
        CryptoServiceClient.builder()
            .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
            .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
            .retry(RetryOptions.of(3))
            .build()) {
      KeyGenerateResult key = client.generateKeyPair();
      System.out.println("generated " + key.algorithm() + " key " + key.keyId());
      SignResult signed = client.sign(new SignRequest(key.keyId(), "pay 100 EUR to ACME"));
      System.out.println("signature: " + signed.signatureBytes().length + " bytes");
      String message = "pay 100 EUR to ACME";
      VerifyResult good =
          client.verify(new VerifyRequest(key.publicKey(), message, signed.signature()));
      VerifyResult tampered =
          client.verify(new VerifyRequest(key.publicKey(), "pay 900 EUR", signed.signature()));
      System.out.println("valid: " + good.valid() + ", tampered valid: " + tampered.valid());
      client.deleteKey(new KeyIdRequest(key.keyId()));
      if (!good.valid() || tampered.valid()) {
        throw new IllegalStateException("verification gave the wrong answer");
      }
    }
  }
}
