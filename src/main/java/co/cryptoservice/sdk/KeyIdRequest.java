// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/keys/export}.
 *
 * @param keyId the server-held key's identifier
 */
public record KeyIdRequest(
    String keyId) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KeyIdRequest {
    Objects.requireNonNull(keyId, "keyId");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyIdRequest")
        .show("keyId", keyId)
        .toString();
  }
}
