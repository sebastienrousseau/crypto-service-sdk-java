// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/password/decrypt}.
 *
 * @param password the password
 * @param ciphertext the ciphertext, as the server returned it
 */
public record PasswordDecryptRequest(String password, String ciphertext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public PasswordDecryptRequest {
    Objects.requireNonNull(password, "password");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("PasswordDecryptRequest")
        .hide("password", password)
        .hide("ciphertext", ciphertext)
        .toString();
  }
}
