// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau

import co.cryptoservice.sdk.Bytes;
import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.HashAlgorithm;
import co.cryptoservice.sdk.HashRequest;
import co.cryptoservice.sdk.HashResult;
import co.cryptoservice.sdk.RetryOptions;

/**
 * SHA-256 of "abc" computed by the server, checked against the FIPS 180-2 test vector.
 *
 * <p>Reads CRYPTO_SERVICE_URL (default http://127.0.0.1:3101) and CRYPTO_SERVICE_API_KEY. Run from
 * the repository root with {@code make examples}.
 */
public final class Hash {

  private static final String ABC_SHA256 =
      "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

  private Hash() {}

  public static void main(String[] args) {
    try (CryptoServiceClient client =
        CryptoServiceClient.builder()
            .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
            .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
            .retry(RetryOptions.of(3))
            .build()) {
      HashResult result = client.hash(new HashRequest(HashAlgorithm.SHA256, "abc"));
      System.out.println(result.algorithm() + " " + result.digest());
      System.out.println(result.digestBytes().length + " bytes");
      if (!ABC_SHA256.equals(Bytes.hex(result.digestBytes()))) {
        throw new IllegalStateException("unexpected digest");
      }
    }
  }
}
