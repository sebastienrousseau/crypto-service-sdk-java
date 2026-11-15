// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/kms/generate-data-key}.
 *
 * @param keyId the server-held key's identifier
 * @param keySpec the data-key specification, or null for the default
 * @param provider the KMS provider, or null for the default
 */
public record KmsGenerateDataKeyRequest(
    String keyId,
    String keySpec,
    String provider) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KmsGenerateDataKeyRequest {
    Objects.requireNonNull(keyId, "keyId");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @return the request
   */
  public static KmsGenerateDataKeyRequest of(String keyId) {
    return new KmsGenerateDataKeyRequest(keyId, null, null);
  }

  /**
   * A copy with {@code keySpec} replaced.
   *
   * @param keySpec the data-key specification, or null for the default
   * @return the new request
   */
  public KmsGenerateDataKeyRequest withKeySpec(String keySpec) {
    return new KmsGenerateDataKeyRequest(keyId, keySpec, provider);
  }

  /**
   * A copy with {@code provider} replaced.
   *
   * @param provider the KMS provider, or null for the default
   * @return the new request
   */
  public KmsGenerateDataKeyRequest withProvider(String provider) {
    return new KmsGenerateDataKeyRequest(keyId, keySpec, provider);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsGenerateDataKeyRequest")
        .show("keyId", keyId)
        .show("keySpec", keySpec)
        .show("provider", provider)
        .toString();
  }
}
