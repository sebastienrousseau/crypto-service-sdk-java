// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/kms/create-key} answers: create a managed key in the designated KMS
 * provider.
 *
 * @param algorithm key algorithm
 * @param createdAt creation time, ISO 8601
 * @param enabled whether the key is enabled
 * @param keyId the provider's key identifier (local keys: local-&lt;hex&gt;)
 * @param provider the provider that served the request (local, aws, gcp, vault)
 * @param usage the key usage, or null for {@code encrypt}
 * @param currentVersion current key version, when versioned, or null when absent
 */
public record KmsKey(
    String algorithm,
    String createdAt,
    boolean enabled,
    String keyId,
    String provider,
    String usage,
    Integer currentVersion) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsKey")
        .show("algorithm", algorithm)
        .show("createdAt", createdAt)
        .show("enabled", enabled)
        .show("keyId", keyId)
        .show("provider", provider)
        .show("usage", usage)
        .show("currentVersion", currentVersion)
        .toString();
  }
}
