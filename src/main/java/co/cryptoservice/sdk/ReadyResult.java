// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Map;

/**
 * What {@code GET /ready} answers: readiness probe.
 *
 * @param checks each check, true when it passes
 * @param status the status
 */
public record ReadyResult(
    Map<String, Boolean> checks,
    String status) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("ReadyResult")
        .show("checks", checks)
        .show("status", status)
        .toString();
  }
}
