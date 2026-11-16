// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * One element of {@code items}.
 *
 * @param id the caller's identifier for this item
 * @param keyId the server-held key's identifier
 * @param message the message, as UTF-8 text
 */
public record StreamSignItem(
    String id,
    String keyId,
    String message) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamSignItem {
    Objects.requireNonNull(id, "id");
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
    return new Redacted("StreamSignItem")
        .show("id", id)
        .show("keyId", keyId)
        .hide("message", message)
        .toString();
  }
}
