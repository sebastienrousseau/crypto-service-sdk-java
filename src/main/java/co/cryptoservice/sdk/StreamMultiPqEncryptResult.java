// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/stream/multi-pq-encrypt} answers: encrypt stream for multiple recipients
 * with hybrid post-quantum AEAD.
 *
 * @param algorithm stream algorithm
 * @param ciphertext the chunked STREAM ciphertext with one key slot per recipient; standard base64
 *     (RFC 4648 section 4), padded
 * @param recipientCount number of recipients
 */
public record StreamMultiPqEncryptResult(String algorithm, String ciphertext, int recipientCount) {

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
    return new Redacted("StreamMultiPqEncryptResult")
        .show("algorithm", algorithm)
        .hide("ciphertext", ciphertext)
        .show("recipientCount", recipientCount)
        .toString();
  }
}
