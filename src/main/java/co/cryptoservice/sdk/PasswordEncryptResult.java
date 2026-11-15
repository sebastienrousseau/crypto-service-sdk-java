// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/password/encrypt} answers: encrypt with password (Argon2id +
 * XChaCha20-Poly1305).
 *
 * @param algorithm envelope algorithm
 * @param encrypted self-describing envelope: version, Argon2id parameters, salt, nonce and
 *     ciphertext; pass it to /v2/password/decrypt as ciphertext; standard base64 (RFC 4648 section
 *     4), padded
 */
public record PasswordEncryptResult(
    String algorithm,
    String encrypted) {

  /**
   * {@link #encrypted()} decoded from base64.
   *
   * @return the bytes, or null when {@code encrypted} is absent
   * @throws IllegalArgumentException when the server sent malformed base64
   */
  @JsonIgnore
  public byte[] encryptedBytes() {
    return Bytes.fromBase64OrNull(encrypted);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("PasswordEncryptResult")
        .show("algorithm", algorithm)
        .hide("encrypted", encrypted)
        .toString();
  }
}
