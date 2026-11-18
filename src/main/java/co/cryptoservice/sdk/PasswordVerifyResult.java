// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/password/verify} answers: verify a password against an Argon2id hash.
 *
 * @param valid whether the password matches
 */
public record PasswordVerifyResult(boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("PasswordVerifyResult").show("valid", valid).toString();
  }
}
