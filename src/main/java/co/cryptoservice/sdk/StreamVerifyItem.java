// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * One element of {@code items}.
 *
 * @param id the caller's identifier for this item
 * @param message the message, as UTF-8 text
 * @param publicKey the public key as hex
 * @param signature the signature as hex
 */
public record StreamVerifyItem(String id, String message, String publicKey, String signature) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamVerifyItem {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(message, "message");
    Objects.requireNonNull(publicKey, "publicKey");
    Objects.requireNonNull(signature, "signature");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamVerifyItem")
        .show("id", id)
        .hide("message", message)
        .hide("publicKey", publicKey)
        .hide("signature", signature)
        .toString();
  }
}
