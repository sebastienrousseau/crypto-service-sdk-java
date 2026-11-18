// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/opaque/register/finish}.
 *
 * @param credentialIdentifier the credential identifier
 * @param record the client's registration record as hex
 * @param suite the OPAQUE suite, or null for the default
 */
public record OpaqueRegisterFinishRequest(
    String credentialIdentifier, String record, OpaqueSuite suite) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public OpaqueRegisterFinishRequest {
    Objects.requireNonNull(credentialIdentifier, "credentialIdentifier");
    Objects.requireNonNull(record, "record");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param credentialIdentifier the credential identifier
   * @param record the client's registration record as hex
   * @return the request
   */
  public static OpaqueRegisterFinishRequest of(String credentialIdentifier, String record) {
    return new OpaqueRegisterFinishRequest(credentialIdentifier, record, null);
  }

  /**
   * A copy with {@code suite} replaced.
   *
   * @param suite the OPAQUE suite, or null for the default
   * @return the new request
   */
  public OpaqueRegisterFinishRequest withSuite(OpaqueSuite suite) {
    return new OpaqueRegisterFinishRequest(credentialIdentifier, record, suite);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueRegisterFinishRequest")
        .hide("credentialIdentifier", credentialIdentifier)
        .hide("record", record)
        .show("suite", suite)
        .toString();
  }
}
