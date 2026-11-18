// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/hpke/open} answers: decrypt (open) an HPKE ciphertext with a server-held
 * key.
 *
 * @param hex the decrypted bytes; lowercase hex
 * @param plaintext the decrypted bytes, decoded as UTF-8 (invalid sequences become U+FFFD); UTF-8
 *     text
 */
public record HpkeOpenResult(String hex, String plaintext) {

  /**
   * {@link #hex() hex()} decoded from hex.
   *
   * @return the bytes, or null when {@code hex} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] hexBytes() {
    return Bytes.fromHexOrNull(hex);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HpkeOpenResult").hide("hex", hex).hide("plaintext", plaintext).toString();
  }
}
