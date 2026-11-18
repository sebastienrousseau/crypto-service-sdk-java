// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code GET /health} answers: health check.
 *
 * @param status the status
 * @param statusCode the statusCode
 * @param uptime process uptime in seconds
 */
public record HealthResult(String status, int statusCode, double uptime) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HealthResult")
        .show("status", status)
        .show("statusCode", statusCode)
        .show("uptime", uptime)
        .toString();
  }
}
