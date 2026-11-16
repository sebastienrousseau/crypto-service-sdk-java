// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * One element of {@code properties}.
 *
 * @param name the name
 * @param value the value
 */
public record CbomProperty(
    String name,
    String value) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("CbomProperty")
        .show("name", name)
        .show("value", value)
        .toString();
  }
}
