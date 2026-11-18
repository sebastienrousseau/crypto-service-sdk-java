// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/kms/unwrap} answers: unwrap a wrapped key using a managed KMS key.
 *
 * @param keyId the provider's key identifier (local keys: local-&lt;hex&gt;)
 * @param provider the provider that served the request (local, aws, gcp, vault)
 * @param unwrappedKey the unwrapped key bytes; lowercase hex
 */
public record KmsUnwrapResult(String keyId, String provider, String unwrappedKey) {

  /**
   * {@link #unwrappedKey() unwrappedKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code unwrappedKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] unwrappedKeyBytes() {
    return Bytes.fromHexOrNull(unwrappedKey);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsUnwrapResult")
        .show("keyId", keyId)
        .show("provider", provider)
        .hide("unwrappedKey", unwrappedKey)
        .toString();
  }
}
