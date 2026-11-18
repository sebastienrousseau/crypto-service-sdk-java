// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/decapsulate} answers: ML-KEM decapsulate.
 *
 * @param algorithm KEM algorithm
 * @param sharedSecret the 32-byte shared secret; lowercase hex
 */
public record KemDecapsulateResult(String algorithm, String sharedSecret) {

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
    return new Redacted("KemDecapsulateResult")
        .show("algorithm", algorithm)
        .hide("sharedSecret", sharedSecret)
        .toString();
  }
}
