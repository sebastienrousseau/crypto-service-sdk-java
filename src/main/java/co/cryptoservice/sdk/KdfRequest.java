// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/kdf}.
 *
 * @param algorithm the algorithm
 * @param password the password
 * @param salt the salt as hex, or null for a random one
 * @param keyLength the derived key length in bytes, or null for the default
 * @param params the parameters
 */
public record KdfRequest(
    KdfAlgorithm algorithm, String password, String salt, Double keyLength, KdfParams params) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KdfRequest {
    Objects.requireNonNull(algorithm, "algorithm");
    Objects.requireNonNull(password, "password");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param algorithm the algorithm
   * @param password the password
   * @return the request
   */
  public static KdfRequest of(KdfAlgorithm algorithm, String password) {
    return new KdfRequest(algorithm, password, null, null, null);
  }

  /**
   * A copy with {@code salt} replaced.
   *
   * @param salt the salt as hex, or null for a random one
   * @return the new request
   */
  public KdfRequest withSalt(String salt) {
    return new KdfRequest(algorithm, password, salt, keyLength, params);
  }

  /**
   * A copy with {@code keyLength} replaced.
   *
   * @param keyLength the derived key length in bytes, or null for the default
   * @return the new request
   */
  public KdfRequest withKeyLength(Double keyLength) {
    return new KdfRequest(algorithm, password, salt, keyLength, params);
  }

  /**
   * A copy with {@code params} replaced.
   *
   * @param params the parameters
   * @return the new request
   */
  public KdfRequest withParams(KdfParams params) {
    return new KdfRequest(algorithm, password, salt, keyLength, params);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KdfRequest")
        .show("algorithm", algorithm)
        .hide("password", password)
        .hide("salt", salt)
        .show("keyLength", keyLength)
        .hide("params", params)
        .toString();
  }
}
