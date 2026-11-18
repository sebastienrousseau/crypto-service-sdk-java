// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/pq/hybrid/encapsulate}.
 *
 * @param x25519PublicKey the X25519 public key as hex
 * @param mlKemPublicKey the ML-KEM public key as hex
 */
public record HybridEncapsulateRequest(String x25519PublicKey, String mlKemPublicKey) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public HybridEncapsulateRequest {
    Objects.requireNonNull(x25519PublicKey, "x25519PublicKey");
    Objects.requireNonNull(mlKemPublicKey, "mlKemPublicKey");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HybridEncapsulateRequest")
        .hide("x25519PublicKey", x25519PublicKey)
        .hide("mlKemPublicKey", mlKemPublicKey)
        .toString();
  }
}
