// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/opaque/register/init}.
 *
 * @param credentialIdentifier the credential identifier
 * @param request the client's registration request as hex
 * @param suite the OPAQUE suite, or null for the default
 */
public record OpaqueRegisterInitRequest(
    String credentialIdentifier,
    String request,
    OpaqueSuite suite) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public OpaqueRegisterInitRequest {
    Objects.requireNonNull(credentialIdentifier, "credentialIdentifier");
    Objects.requireNonNull(request, "request");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param credentialIdentifier the credential identifier
   * @param request the client's registration request as hex
   * @return the request
   */
  public static OpaqueRegisterInitRequest of(String credentialIdentifier, String request) {
    return new OpaqueRegisterInitRequest(credentialIdentifier, request, null);
  }

  /**
   * A copy with {@code suite} replaced.
   *
   * @param suite the OPAQUE suite, or null for the default
   * @return the new request
   */
  public OpaqueRegisterInitRequest withSuite(OpaqueSuite suite) {
    return new OpaqueRegisterInitRequest(credentialIdentifier, request, suite);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueRegisterInitRequest")
        .hide("credentialIdentifier", credentialIdentifier)
        .hide("request", request)
        .show("suite", suite)
        .toString();
  }
}
