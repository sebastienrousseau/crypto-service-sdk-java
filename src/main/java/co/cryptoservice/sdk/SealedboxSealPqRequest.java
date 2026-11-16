// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/sealedbox/seal-pq}.
 *
 * @param x25519PublicKey the X25519 public key as hex
 * @param mlKemPublicKey the ML-KEM public key as hex
 * @param plaintext the plaintext, as UTF-8 text
 */
public record SealedboxSealPqRequest(
    String x25519PublicKey,
    String mlKemPublicKey,
    String plaintext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public SealedboxSealPqRequest {
    Objects.requireNonNull(x25519PublicKey, "x25519PublicKey");
    Objects.requireNonNull(mlKemPublicKey, "mlKemPublicKey");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SealedboxSealPqRequest")
        .hide("x25519PublicKey", x25519PublicKey)
        .hide("mlKemPublicKey", mlKemPublicKey)
        .hide("plaintext", plaintext)
        .toString();
  }
}
