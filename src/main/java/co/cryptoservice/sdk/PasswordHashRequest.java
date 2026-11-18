// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/password/hash}.
 *
 * @param password the password
 * @param timeCost the Argon2id passes, or null for the default
 * @param memoryCost the Argon2id memory in KiB, or null for the default
 * @param parallelism the Argon2id lanes, or null for the default
 */
public record PasswordHashRequest(
    String password, Integer timeCost, Integer memoryCost, Integer parallelism) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public PasswordHashRequest {
    Objects.requireNonNull(password, "password");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param password the password
   * @return the request
   */
  public static PasswordHashRequest of(String password) {
    return new PasswordHashRequest(password, null, null, null);
  }

  /**
   * A copy with {@code timeCost} replaced.
   *
   * @param timeCost the Argon2id passes, or null for the default
   * @return the new request
   */
  public PasswordHashRequest withTimeCost(Integer timeCost) {
    return new PasswordHashRequest(password, timeCost, memoryCost, parallelism);
  }

  /**
   * A copy with {@code memoryCost} replaced.
   *
   * @param memoryCost the Argon2id memory in KiB, or null for the default
   * @return the new request
   */
  public PasswordHashRequest withMemoryCost(Integer memoryCost) {
    return new PasswordHashRequest(password, timeCost, memoryCost, parallelism);
  }

  /**
   * A copy with {@code parallelism} replaced.
   *
   * @param parallelism the Argon2id lanes, or null for the default
   * @return the new request
   */
  public PasswordHashRequest withParallelism(Integer parallelism) {
    return new PasswordHashRequest(password, timeCost, memoryCost, parallelism);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("PasswordHashRequest")
        .hide("password", password)
        .show("timeCost", timeCost)
        .show("memoryCost", memoryCost)
        .show("parallelism", parallelism)
        .toString();
  }
}
