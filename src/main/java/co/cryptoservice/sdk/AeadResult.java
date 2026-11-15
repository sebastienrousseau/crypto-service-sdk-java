// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/encrypt} answers: encrypt with XChaCha20-Poly1305.
 *
 * @param algorithm AEAD algorithm
 * @param ciphertext nonce (24 bytes) || ciphertext || Poly1305 tag (16 bytes); standard base64 (RFC
 *     4648 section 4), padded
 */
public record AeadResult(
    String algorithm,
    String ciphertext) {

  /**
   * {@link #ciphertext()} decoded from base64.
   *
   * @return the bytes, or null when {@code ciphertext} is absent
   * @throws IllegalArgumentException when the server sent malformed base64
   */
  @JsonIgnore
  public byte[] ciphertextBytes() {
    return Bytes.fromBase64OrNull(ciphertext);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("AeadResult")
        .show("algorithm", algorithm)
        .hide("ciphertext", ciphertext)
        .toString();
  }
}
