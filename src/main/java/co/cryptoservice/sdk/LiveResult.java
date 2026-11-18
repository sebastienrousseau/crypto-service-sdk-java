// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * What {@code GET /live} answers: liveness probe.
 *
 * @param status the status
 * @param uptime process uptime in whole seconds
 */
public record LiveResult(String status, int uptime) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("LiveResult").show("status", status).show("uptime", uptime).toString();
  }
}
