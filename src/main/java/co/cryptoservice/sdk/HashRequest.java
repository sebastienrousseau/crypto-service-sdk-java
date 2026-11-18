// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/hash}.
 *
 * @param algorithm the algorithm
 * @param data the input, as UTF-8 text
 */
public record HashRequest(HashAlgorithm algorithm, String data) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public HashRequest {
    Objects.requireNonNull(algorithm, "algorithm");
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
    return new Redacted("HashRequest").show("algorithm", algorithm).hide("data", data).toString();
  }
}
