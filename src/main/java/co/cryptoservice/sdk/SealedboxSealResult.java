// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/sealedbox/seal} answers: anonymous public-key encryption (X25519 sealed
 * box).
 *
 * @param algorithm sealed-box algorithm
 * @param sealed ephemeral X25519 public key (32 bytes) || nonce (24 bytes) || ciphertext || tag (16
 *     bytes); standard base64 (RFC 4648 section 4), padded
 */
public record SealedboxSealResult(String algorithm, String sealed) {

  /**
   * {@link #sealed()} decoded from base64.
   *
   * @return the bytes, or null when {@code sealed} is absent
   * @throws IllegalArgumentException when the server sent malformed base64
   */
  @JsonIgnore
  public byte[] sealedBytes() {
    return Bytes.fromBase64OrNull(sealed);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SealedboxSealResult")
        .show("algorithm", algorithm)
        .hide("sealed", sealed)
        .toString();
  }
}
