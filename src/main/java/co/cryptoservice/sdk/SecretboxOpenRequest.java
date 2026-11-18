// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/secretbox/open}.
 *
 * @param key the 256-bit key as hex (64 characters)
 * @param ciphertext the ciphertext, as the server returned it
 * @param aad additional authenticated data, or null
 */
public record SecretboxOpenRequest(String key, String ciphertext, String aad) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public SecretboxOpenRequest {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param key the 256-bit key as hex (64 characters)
   * @param ciphertext the ciphertext, as the server returned it
   * @return the request
   */
  public static SecretboxOpenRequest of(String key, String ciphertext) {
    return new SecretboxOpenRequest(key, ciphertext, null);
  }

  /**
   * A copy with {@code aad} replaced.
   *
   * @param aad additional authenticated data, or null
   * @return the new request
   */
  public SecretboxOpenRequest withAad(String aad) {
    return new SecretboxOpenRequest(key, ciphertext, aad);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SecretboxOpenRequest")
        .hide("key", key)
        .hide("ciphertext", ciphertext)
        .hide("aad", aad)
        .toString();
  }
}
