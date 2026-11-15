// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/encapsulate} answers: ML-KEM encapsulate.
 *
 * @param algorithm KEM algorithm
 * @param ciphertext the 1088-byte ML-KEM-768 ciphertext; lowercase hex
 * @param sharedSecret the 32-byte shared secret; lowercase hex
 */
public record MlKemEncapsulateResult(
    String algorithm,
    String ciphertext,
    String sharedSecret) {

  /**
   * {@link #ciphertext()} decoded from hex.
   *
   * @return the bytes, or null when {@code ciphertext} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] ciphertextBytes() {
    return Bytes.fromHexOrNull(ciphertext);
  }

  /**
   * {@link #sharedSecret()} decoded from hex.
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
    return new Redacted("MlKemEncapsulateResult")
        .show("algorithm", algorithm)
        .hide("ciphertext", ciphertext)
        .hide("sharedSecret", sharedSecret)
        .toString();
  }
}
