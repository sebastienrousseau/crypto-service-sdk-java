// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/decrypt} answers: decrypt with XChaCha20-Poly1305.
 *
 * @param plaintext the decrypted bytes, decoded as UTF-8 (invalid sequences become U+FFFD); UTF-8
 *     text
 */
public record DecryptResult(String plaintext) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("DecryptResult").hide("plaintext", plaintext).toString();
  }
}
