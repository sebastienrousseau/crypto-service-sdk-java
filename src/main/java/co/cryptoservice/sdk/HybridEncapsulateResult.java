// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/hybrid/encapsulate} answers: hybrid encapsulate (X25519 + ML-KEM-768).
 *
 * @param algorithm hybrid KEM algorithm
 * @param combiner shared-secret combiner
 * @param mlKemCiphertext the 1088-byte ML-KEM-768 ciphertext; lowercase hex
 * @param sharedSecret the 32-byte shared secret; lowercase hex
 * @param x25519EphemeralPublic the 32-byte ephemeral X25519 public key; lowercase hex
 */
public record HybridEncapsulateResult(
    String algorithm,
    String combiner,
    String mlKemCiphertext,
    String sharedSecret,
    String x25519EphemeralPublic) {

  /**
   * {@link #mlKemCiphertext() mlKemCiphertext()} decoded from hex.
   *
   * @return the bytes, or null when {@code mlKemCiphertext} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] mlKemCiphertextBytes() {
    return Bytes.fromHexOrNull(mlKemCiphertext);
  }

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
   * {@link #x25519EphemeralPublic() x25519EphemeralPublic()} decoded from hex.
   *
   * @return the bytes, or null when {@code x25519EphemeralPublic} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] x25519EphemeralPublicBytes() {
    return Bytes.fromHexOrNull(x25519EphemeralPublic);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HybridEncapsulateResult")
        .show("algorithm", algorithm)
        .show("combiner", combiner)
        .hide("mlKemCiphertext", mlKemCiphertext)
        .hide("sharedSecret", sharedSecret)
        .hide("x25519EphemeralPublic", x25519EphemeralPublic)
        .toString();
  }
}
