// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/stream/multi-pq-decrypt} answers: decrypt multi-recipient stream with
 * server-held hybrid post-quantum key.
 *
 * @param plaintext the decrypted bytes, decoded as UTF-8 (invalid sequences become U+FFFD); UTF-8
 *     text
 * @param recipientId the recipient slot that opened the ciphertext
 */
public record StreamMultiPqDecryptResult(
    String plaintext,
    String recipientId) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamMultiPqDecryptResult")
        .hide("plaintext", plaintext)
        .show("recipientId", recipientId)
        .toString();
  }
}
