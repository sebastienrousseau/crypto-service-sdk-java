// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/opaque/login/finish}.
 *
 * @param sessionId the login session identifier
 * @param ke3 the client's KE3 message as hex
 */
public record OpaqueLoginFinishRequest(String sessionId, String ke3) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public OpaqueLoginFinishRequest {
    Objects.requireNonNull(sessionId, "sessionId");
    Objects.requireNonNull(ke3, "ke3");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueLoginFinishRequest")
        .hide("sessionId", sessionId)
        .hide("ke3", ke3)
        .toString();
  }
}
