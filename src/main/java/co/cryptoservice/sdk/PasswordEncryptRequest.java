// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/password/encrypt}.
 *
 * @param password the password
 * @param plaintext the plaintext, as UTF-8 text
 */
public record PasswordEncryptRequest(String password, String plaintext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public PasswordEncryptRequest {
    Objects.requireNonNull(password, "password");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("PasswordEncryptRequest")
        .hide("password", password)
        .hide("plaintext", plaintext)
        .toString();
  }
}
