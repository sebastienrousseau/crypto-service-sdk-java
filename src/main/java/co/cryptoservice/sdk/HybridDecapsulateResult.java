// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/hybrid/decapsulate} answers: hybrid decapsulate (X25519 + ML-KEM-768).
 *
 * @param algorithm hybrid KEM algorithm
 * @param combiner shared-secret combiner
 * @param sharedSecret the 32-byte shared secret; lowercase hex
 */
public record HybridDecapsulateResult(String algorithm, String combiner, String sharedSecret) {

  /**
   * {@link #sharedSecret() sharedSecret()} decoded from hex.
   *
   * @return the bytes, or null when {@code sharedSecret} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] sharedSecretBytes() {
    return Bytes.fromHexOrNull(sharedSecret);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HybridDecapsulateResult")
        .show("algorithm", algorithm)
        .show("combiner", combiner)
        .hide("sharedSecret", sharedSecret)
        .toString();
  }
}
