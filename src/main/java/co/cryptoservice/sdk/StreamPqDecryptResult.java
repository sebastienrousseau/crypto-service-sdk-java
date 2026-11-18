// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/stream/pq-decrypt} answers: decrypt stream with server-held hybrid
 * post-quantum key.
 *
 * @param plaintext the decrypted bytes, decoded as UTF-8 (invalid sequences become U+FFFD); UTF-8
 *     text
 */
public record StreamPqDecryptResult(String plaintext) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamPqDecryptResult").hide("plaintext", plaintext).toString();
  }
}
