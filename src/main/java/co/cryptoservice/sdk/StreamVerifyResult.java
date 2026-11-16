// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;

/**
 * What {@code POST /v2/stream/verify} answers: batch verify digital signatures.
 *
 * @param allValid whether every signature is valid
 * @param count number of items
 * @param results the results
 * @param validCount number of valid signatures
 */
public record StreamVerifyResult(
    boolean allValid,
    int count,
    List<StreamVerifyOutcome> results,
    int validCount) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamVerifyResult")
        .show("allValid", allValid)
        .show("count", count)
        .hide("results", results)
        .show("validCount", validCount)
        .toString();
  }
}
