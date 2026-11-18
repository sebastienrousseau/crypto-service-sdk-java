// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Map;

/**
 * What {@code POST /v2/kms/encrypt} answers: encrypt plaintext with a managed KMS key.
 *
 * @param ciphertext the ciphertext; opaque, provider-specific string; pass it back unchanged
 * @param keyId the provider's key identifier (local keys: local-&lt;hex&gt;)
 * @param provider the provider that served the request (local, aws, gcp, vault)
 * @param context encryption context, when the provider echoes it, or null when absent
 * @param keyVersion key version used, when versioned, or null when absent
 */
public record KmsEncryptResult(
    String ciphertext,
    String keyId,
    String provider,
    Map<String, String> context,
    Integer keyVersion) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsEncryptResult")
        .hide("ciphertext", ciphertext)
        .show("keyId", keyId)
        .show("provider", provider)
        .hide("context", context)
        .show("keyVersion", keyVersion)
        .toString();
  }
}
