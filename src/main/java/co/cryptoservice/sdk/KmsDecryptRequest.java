// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Map;
import java.util.Objects;

/**
 * The body of {@code POST /v2/kms/decrypt}.
 *
 * @param keyId the server-held key's identifier
 * @param ciphertext the ciphertext, as the server returned it
 * @param provider the KMS provider, or null for the default
 * @param context the encryption context, or null
 */
public record KmsDecryptRequest(
    String keyId, String ciphertext, String provider, Map<String, String> context) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KmsDecryptRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @param ciphertext the ciphertext, as the server returned it
   * @return the request
   */
  public static KmsDecryptRequest of(String keyId, String ciphertext) {
    return new KmsDecryptRequest(keyId, ciphertext, null, null);
  }

  /**
   * A copy with {@code provider} replaced.
   *
   * @param provider the KMS provider, or null for the default
   * @return the new request
   */
  public KmsDecryptRequest withProvider(String provider) {
    return new KmsDecryptRequest(keyId, ciphertext, provider, context);
  }

  /**
   * A copy with {@code context} replaced.
   *
   * @param context the encryption context, or null
   * @return the new request
   */
  public KmsDecryptRequest withContext(Map<String, String> context) {
    return new KmsDecryptRequest(keyId, ciphertext, provider, context);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsDecryptRequest")
        .show("keyId", keyId)
        .hide("ciphertext", ciphertext)
        .show("provider", provider)
        .hide("context", context)
        .toString();
  }
}
