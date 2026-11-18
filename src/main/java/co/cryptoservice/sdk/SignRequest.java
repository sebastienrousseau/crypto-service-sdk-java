// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/sign}.
 *
 * @param keyId the server-held key's identifier
 * @param message the message, as UTF-8 text
 */
public record SignRequest(String keyId, String message) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public SignRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(message, "message");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SignRequest").show("keyId", keyId).hide("message", message).toString();
  }
}
