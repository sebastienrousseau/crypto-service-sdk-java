// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/password/hash} answers: hash a password with Argon2id.
 *
 * @param algorithm password hashing algorithm
 * @param hash the 32-byte Argon2id hash; lowercase hex
 * @param params argon2id parameters; pass them back to verify
 * @param phc the same hash as a PHC string ($argon2id$v=19$m=..,t=..,p=..$salt$hash, unpadded
 *     base64 fields)
 * @param salt the random 16-byte salt; lowercase hex
 */
public record PasswordHashResult(
    String algorithm,
    String hash,
    Argon2Params params,
    String phc,
    String salt) {

  /**
   * {@link #hash()} decoded from hex.
   *
   * @return the bytes, or null when {@code hash} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] hashBytes() {
    return Bytes.fromHexOrNull(hash);
  }

  /**
   * {@link #salt()} decoded from hex.
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
    return new Redacted("PasswordHashResult")
        .show("algorithm", algorithm)
        .hide("hash", hash)
        .hide("params", params)
        .hide("phc", phc)
        .hide("salt", salt)
        .toString();
  }
}
