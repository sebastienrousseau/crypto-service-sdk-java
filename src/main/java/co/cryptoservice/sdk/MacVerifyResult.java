// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/hmac/verify} answers: verify an HMAC.
 *
 * @param algorithm the algorithm
 * @param valid whether the tag matches
 */
public record MacVerifyResult(
    String algorithm,
    boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MacVerifyResult")
        .show("algorithm", algorithm)
        .show("valid", valid)
        .toString();
  }
}
