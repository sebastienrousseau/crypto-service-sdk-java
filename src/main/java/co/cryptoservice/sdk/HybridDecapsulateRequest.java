// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/pq/hybrid/decapsulate}.
 *
 * @param keyId the server-held key's identifier
 * @param x25519EphemeralPublic the ephemeral X25519 public key as hex
 * @param mlKemCiphertext the ML-KEM ciphertext as hex
 */
public record HybridDecapsulateRequest(
    String keyId, String x25519EphemeralPublic, String mlKemCiphertext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public HybridDecapsulateRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(x25519EphemeralPublic, "x25519EphemeralPublic");
    Objects.requireNonNull(mlKemCiphertext, "mlKemCiphertext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HybridDecapsulateRequest")
        .show("keyId", keyId)
        .hide("x25519EphemeralPublic", x25519EphemeralPublic)
        .hide("mlKemCiphertext", mlKemCiphertext)
        .toString();
  }
}
