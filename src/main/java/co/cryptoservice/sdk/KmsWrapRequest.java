// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Map;
import java.util.Objects;

/**
 * The body of {@code POST /v2/kms/wrap}.
 *
 * @param keyId the server-held key's identifier
 * @param unwrappedKey the unwrapped key
 * @param provider the KMS provider, or null for the default
 * @param context the encryption context, or null
 */
public record KmsWrapRequest(
    String keyId, String unwrappedKey, String provider, Map<String, String> context) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KmsWrapRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(unwrappedKey, "unwrappedKey");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @param unwrappedKey the unwrapped key
   * @return the request
   */
  public static KmsWrapRequest of(String keyId, String unwrappedKey) {
    return new KmsWrapRequest(keyId, unwrappedKey, null, null);
  }

  /**
   * A copy with {@code provider} replaced.
   *
   * @param provider the KMS provider, or null for the default
   * @return the new request
   */
  public KmsWrapRequest withProvider(String provider) {
    return new KmsWrapRequest(keyId, unwrappedKey, provider, context);
  }

  /**
   * A copy with {@code context} replaced.
   *
   * @param context the encryption context, or null
   * @return the new request
   */
  public KmsWrapRequest withContext(Map<String, String> context) {
    return new KmsWrapRequest(keyId, unwrappedKey, provider, context);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsWrapRequest")
        .show("keyId", keyId)
        .hide("unwrappedKey", unwrappedKey)
        .show("provider", provider)
        .hide("context", context)
        .toString();
  }
}
