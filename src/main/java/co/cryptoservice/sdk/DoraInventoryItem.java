// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One element of {@code cryptographicInventory}.
 *
 * @param fipsCompliance what the package implements (name kept for clients)
 * @param packageName the package
 * @param status the status
 * @param version the package version, or unknown
 */
public record DoraInventoryItem(
    String fipsCompliance,
    @JsonProperty("package") String packageName,
    String status,
    String version) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("DoraInventoryItem")
        .show("fipsCompliance", fipsCompliance)
        .show("packageName", packageName)
        .show("status", status)
        .show("version", version)
        .toString();
  }
}
