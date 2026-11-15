// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * One element of {@code results}.
 *
 * @param id the item's id
 * @param valid whether the item's signature is valid
 */
public record StreamVerifyOutcome(
    String id,
    boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamVerifyOutcome")
        .show("id", id)
        .show("valid", valid)
        .toString();
  }
}
