// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Map;
import java.util.Objects;

/**
 * The body of {@code POST /v2/kms/encrypt}.
 *
 * @param keyId the server-held key's identifier
 * @param plaintext the plaintext, as UTF-8 text
 * @param encoding how plaintext is encoded, or null for UTF-8
 * @param provider the KMS provider, or null for the default
 * @param context the encryption context, or null
 */
public record KmsEncryptRequest(
    String keyId,
    String plaintext,
    KmsEncoding encoding,
    String provider,
    Map<String, String> context) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KmsEncryptRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @param plaintext the plaintext, as UTF-8 text
   * @return the request
   */
  public static KmsEncryptRequest of(String keyId, String plaintext) {
    return new KmsEncryptRequest(keyId, plaintext, null, null, null);
  }

  /**
   * A copy with {@code encoding} replaced.
   *
   * @param encoding how plaintext is encoded, or null for UTF-8
   * @return the new request
   */
  public KmsEncryptRequest withEncoding(KmsEncoding encoding) {
    return new KmsEncryptRequest(keyId, plaintext, encoding, provider, context);
  }

  /**
   * A copy with {@code provider} replaced.
   *
   * @param provider the KMS provider, or null for the default
   * @return the new request
   */
  public KmsEncryptRequest withProvider(String provider) {
    return new KmsEncryptRequest(keyId, plaintext, encoding, provider, context);
  }

  /**
   * A copy with {@code context} replaced.
   *
   * @param context the encryption context, or null
   * @return the new request
   */
  public KmsEncryptRequest withContext(Map<String, String> context) {
    return new KmsEncryptRequest(keyId, plaintext, encoding, provider, context);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsEncryptRequest")
        .show("keyId", keyId)
        .hide("plaintext", plaintext)
        .show("encoding", encoding)
        .show("provider", provider)
        .hide("context", context)
        .toString();
  }
}
