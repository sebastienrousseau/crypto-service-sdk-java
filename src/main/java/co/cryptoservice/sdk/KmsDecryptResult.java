// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/kms/decrypt} answers: decrypt ciphertext with a managed KMS key.
 *
 * @param hex the decrypted bytes; lowercase hex
 * @param keyId the provider's key identifier (local keys: local-&lt;hex&gt;)
 * @param plaintext the decrypted bytes, decoded as UTF-8 (invalid sequences become U+FFFD); UTF-8
 *     text
 * @param provider the provider that served the request (local, aws, gcp, vault)
 */
public record KmsDecryptResult(
    String hex,
    String keyId,
    String plaintext,
    String provider) {

  /**
   * {@link #hex()} decoded from hex.
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
    return new Redacted("KmsDecryptResult")
        .hide("hex", hex)
        .show("keyId", keyId)
        .hide("plaintext", plaintext)
        .show("provider", provider)
        .toString();
  }
}
