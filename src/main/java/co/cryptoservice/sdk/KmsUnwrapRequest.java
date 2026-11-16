// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Map;
import java.util.Objects;

/**
 * The body of {@code POST /v2/kms/unwrap}.
 *
 * @param keyId the server-held key's identifier
 * @param wrappedKey the wrapped key, as the server returned it
 * @param provider the KMS provider, or null for the default
 * @param context the encryption context, or null
 */
public record KmsUnwrapRequest(
    String keyId,
    String wrappedKey,
    String provider,
    Map<String, String> context) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KmsUnwrapRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(wrappedKey, "wrappedKey");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @param wrappedKey the wrapped key, as the server returned it
   * @return the request
   */
  public static KmsUnwrapRequest of(String keyId, String wrappedKey) {
    return new KmsUnwrapRequest(keyId, wrappedKey, null, null);
  }

  /**
   * A copy with {@code provider} replaced.
   *
   * @param provider the KMS provider, or null for the default
   * @return the new request
   */
  public KmsUnwrapRequest withProvider(String provider) {
    return new KmsUnwrapRequest(keyId, wrappedKey, provider, context);
  }

  /**
   * A copy with {@code context} replaced.
   *
   * @param context the encryption context, or null
   * @return the new request
   */
  public KmsUnwrapRequest withContext(Map<String, String> context) {
    return new KmsUnwrapRequest(keyId, wrappedKey, provider, context);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsUnwrapRequest")
        .show("keyId", keyId)
        .hide("wrappedKey", wrappedKey)
        .show("provider", provider)
        .hide("context", context)
        .toString();
  }
}
