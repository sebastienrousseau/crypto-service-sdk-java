// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/hpke/seal} answers: encrypt (seal) a message using HPKE (RFC 9180).
 *
 * @param ciphertext the AEAD ciphertext, tag included; lowercase hex
 * @param encapsulatedKey the KEM encapsulated key (enc); lowercase hex
 */
public record HpkeSealResult(String ciphertext, String encapsulatedKey) {

  /**
   * {@link #ciphertext() ciphertext()} decoded from hex.
   *
   * @return the bytes, or null when {@code ciphertext} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] ciphertextBytes() {
    return Bytes.fromHexOrNull(ciphertext);
  }

  /**
   * {@link #encapsulatedKey() encapsulatedKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code encapsulatedKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] encapsulatedKeyBytes() {
    return Bytes.fromHexOrNull(encapsulatedKey);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HpkeSealResult")
        .hide("ciphertext", ciphertext)
        .hide("encapsulatedKey", encapsulatedKey)
        .toString();
  }
}
