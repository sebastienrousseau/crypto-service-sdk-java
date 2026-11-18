// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/opaque/register/finish} answers: finish OPAQUE credential registration.
 *
 * @param credentialIdentifier the identifier, as the request gave it
 * @param status always registered
 */
public record OpaqueRegisterFinishResult(String credentialIdentifier, String status) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueRegisterFinishResult")
        .hide("credentialIdentifier", credentialIdentifier)
        .show("status", status)
        .toString();
  }
}
