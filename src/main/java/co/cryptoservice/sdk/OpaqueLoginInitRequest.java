// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/opaque/login/init}.
 *
 * @param credentialIdentifier the credential identifier
 * @param ke1 the client's KE1 message as hex
 * @param suite the OPAQUE suite, or null for the default
 */
public record OpaqueLoginInitRequest(String credentialIdentifier, String ke1, OpaqueSuite suite) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public OpaqueLoginInitRequest {
    Objects.requireNonNull(credentialIdentifier, "credentialIdentifier");
    Objects.requireNonNull(ke1, "ke1");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param credentialIdentifier the credential identifier
   * @param ke1 the client's KE1 message as hex
   * @return the request
   */
  public static OpaqueLoginInitRequest of(String credentialIdentifier, String ke1) {
    return new OpaqueLoginInitRequest(credentialIdentifier, ke1, null);
  }

  /**
   * A copy with {@code suite} replaced.
   *
   * @param suite the OPAQUE suite, or null for the default
   * @return the new request
   */
  public OpaqueLoginInitRequest withSuite(OpaqueSuite suite) {
    return new OpaqueLoginInitRequest(credentialIdentifier, ke1, suite);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueLoginInitRequest")
        .hide("credentialIdentifier", credentialIdentifier)
        .hide("ke1", ke1)
        .show("suite", suite)
        .toString();
  }
}
