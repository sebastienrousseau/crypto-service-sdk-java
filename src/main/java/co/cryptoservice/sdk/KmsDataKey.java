// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/kms/generate-data-key} answers: generate a Data Encryption Key (DEK) via KMS
 * provider.
 *
 * @param ciphertext the data key, encrypted by the KMS key; opaque, provider-specific string; pass
 *     it back unchanged
 * @param keyId the provider's key identifier (local keys: local-&lt;hex&gt;)
 * @param plaintext the data key bytes; use, then discard; lowercase hex
 * @param provider the provider that served the request (local, aws, gcp, vault)
 */
public record KmsDataKey(String ciphertext, String keyId, String plaintext, String provider) {

  /**
   * {@link #plaintext()} decoded from hex.
   *
   * @return the bytes, or null when {@code plaintext} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] plaintextBytes() {
    return Bytes.fromHexOrNull(plaintext);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsDataKey")
        .hide("ciphertext", ciphertext)
        .show("keyId", keyId)
        .hide("plaintext", plaintext)
        .show("provider", provider)
        .toString();
  }
}
