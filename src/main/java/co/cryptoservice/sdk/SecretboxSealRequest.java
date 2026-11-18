// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/secretbox/seal}.
 *
 * @param key the 256-bit key as hex (64 characters)
 * @param plaintext the plaintext, as UTF-8 text
 * @param aad additional authenticated data, or null
 */
public record SecretboxSealRequest(String key, String plaintext, String aad) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public SecretboxSealRequest {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param key the 256-bit key as hex (64 characters)
   * @param plaintext the plaintext, as UTF-8 text
   * @return the request
   */
  public static SecretboxSealRequest of(String key, String plaintext) {
    return new SecretboxSealRequest(key, plaintext, null);
  }

  /**
   * A copy with {@code aad} replaced.
   *
   * @param aad additional authenticated data, or null
   * @return the new request
   */
  public SecretboxSealRequest withAad(String aad) {
    return new SecretboxSealRequest(key, plaintext, aad);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SecretboxSealRequest")
        .hide("key", key)
        .hide("plaintext", plaintext)
        .hide("aad", aad)
        .toString();
  }
}
