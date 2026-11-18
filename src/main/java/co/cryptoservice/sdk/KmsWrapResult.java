// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/kms/wrap} answers: wrap an existing key using a managed KMS key.
 *
 * @param keyId the provider's key identifier (local keys: local-&lt;hex&gt;)
 * @param provider the provider that served the request (local, aws, gcp, vault)
 * @param wrappedKey the wrapped key; opaque, provider-specific string; pass it back unchanged
 */
public record KmsWrapResult(String keyId, String provider, String wrappedKey) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsWrapResult")
        .show("keyId", keyId)
        .show("provider", provider)
        .hide("wrappedKey", wrappedKey)
        .toString();
  }
}
