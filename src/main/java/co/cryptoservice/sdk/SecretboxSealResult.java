// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/secretbox/seal} answers: encrypt with XChaCha20-Poly1305 (secretbox seal).
 *
 * @param algorithm AEAD algorithm
 * @param sealed nonce (24 bytes) || ciphertext || Poly1305 tag (16 bytes); standard base64 (RFC
 *     4648 section 4), padded
 */
public record SecretboxSealResult(String algorithm, String sealed) {

  /**
   * {@link #sealed() sealed()} decoded from base64.
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
    return new Redacted("SecretboxSealResult")
        .show("algorithm", algorithm)
        .hide("sealed", sealed)
        .toString();
  }
}
