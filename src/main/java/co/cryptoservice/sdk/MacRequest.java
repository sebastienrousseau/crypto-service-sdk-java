// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/hmac}.
 *
 * @param algorithm the algorithm
 * @param key the 256-bit key as hex (64 characters)
 * @param data the input, as UTF-8 text
 */
public record MacRequest(HmacAlgorithm algorithm, String key, String data) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public MacRequest {
    Objects.requireNonNull(algorithm, "algorithm");
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(data, "data");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MacRequest")
        .show("algorithm", algorithm)
        .hide("key", key)
        .hide("data", data)
        .toString();
  }
}
