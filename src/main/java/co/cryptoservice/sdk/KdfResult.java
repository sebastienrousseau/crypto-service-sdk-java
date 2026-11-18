// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/kdf} answers: derive a key from a password.
 *
 * @param algorithm the algorithm
 * @param derivedKey the derived key; lowercase hex
 * @param keyLength derived key length in bytes
 * @param salt the salt used: the request's, or 16 random bytes; lowercase hex
 */
public record KdfResult(String algorithm, String derivedKey, int keyLength, String salt) {

  /**
   * {@link #derivedKey() derivedKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code derivedKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] derivedKeyBytes() {
    return Bytes.fromHexOrNull(derivedKey);
  }

  /**
   * {@link #salt() salt()} decoded from hex.
   *
   * @return the bytes, or null when {@code salt} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] saltBytes() {
    return Bytes.fromHexOrNull(salt);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KdfResult")
        .show("algorithm", algorithm)
        .hide("derivedKey", derivedKey)
        .show("keyLength", keyLength)
        .hide("salt", salt)
        .toString();
  }
}
