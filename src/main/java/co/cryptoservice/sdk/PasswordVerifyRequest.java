// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/password/verify}.
 *
 * @param password the password
 * @param hash the stored hash as hex
 * @param salt the salt as hex, or null for a random one
 * @param params the parameters
 */
public record PasswordVerifyRequest(
    String password, String hash, String salt, Argon2Params params) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public PasswordVerifyRequest {
    Objects.requireNonNull(password, "password");
    Objects.requireNonNull(hash, "hash");
    Objects.requireNonNull(salt, "salt");
    Objects.requireNonNull(params, "params");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("PasswordVerifyRequest")
        .hide("password", password)
        .hide("hash", hash)
        .hide("salt", salt)
        .hide("params", params)
        .toString();
  }
}
