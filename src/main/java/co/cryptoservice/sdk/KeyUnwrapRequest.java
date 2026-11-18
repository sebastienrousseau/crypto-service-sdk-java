// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/keys/unwrap}.
 *
 * @param kek the key-encryption key as hex
 * @param wrappedKey the wrapped key, as the server returned it
 * @param algorithm the algorithm
 */
public record KeyUnwrapRequest(String kek, String wrappedKey, KeyWrapAlgorithm algorithm) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KeyUnwrapRequest {
    Objects.requireNonNull(kek, "kek");
    Objects.requireNonNull(wrappedKey, "wrappedKey");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param kek the key-encryption key as hex
   * @param wrappedKey the wrapped key, as the server returned it
   * @return the request
   */
  public static KeyUnwrapRequest of(String kek, String wrappedKey) {
    return new KeyUnwrapRequest(kek, wrappedKey, null);
  }

  /**
   * A copy with {@code algorithm} replaced.
   *
   * @param algorithm the algorithm
   * @return the new request
   */
  public KeyUnwrapRequest withAlgorithm(KeyWrapAlgorithm algorithm) {
    return new KeyUnwrapRequest(kek, wrappedKey, algorithm);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyUnwrapRequest")
        .hide("kek", kek)
        .hide("wrappedKey", wrappedKey)
        .show("algorithm", algorithm)
        .toString();
  }
}
