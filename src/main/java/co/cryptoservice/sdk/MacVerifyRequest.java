// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/hmac/verify}.
 *
 * @param algorithm the algorithm
 * @param key the 256-bit key as hex (64 characters)
 * @param data the input, as UTF-8 text
 * @param mac the MAC as hex
 */
public record MacVerifyRequest(HmacAlgorithm algorithm, String key, String data, String mac) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public MacVerifyRequest {
    Objects.requireNonNull(algorithm, "algorithm");
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(data, "data");
    Objects.requireNonNull(mac, "mac");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MacVerifyRequest")
        .show("algorithm", algorithm)
        .hide("key", key)
        .hide("data", data)
        .hide("mac", mac)
        .toString();
  }
}
