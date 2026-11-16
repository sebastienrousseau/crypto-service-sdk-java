// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;

/**
 * One element of {@code components}.
 *
 * @param name the name, or null when absent
 * @param properties the properties, or null when absent
 * @param type the recipient type, or null when absent
 * @param version the version, or null when absent
 */
public record CbomComponent(
    String name,
    List<CbomProperty> properties,
    String type,
    String version) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("CbomComponent")
        .show("name", name)
        .hide("properties", properties)
        .show("type", type)
        .show("version", version)
        .toString();
  }
}
