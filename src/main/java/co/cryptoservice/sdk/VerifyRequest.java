// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/verify}.
 *
 * @param publicKey the public key as hex
 * @param message the message, as UTF-8 text
 * @param signature the signature as hex
 */
public record VerifyRequest(
    String publicKey,
    String message,
    String signature) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public VerifyRequest {
    Objects.requireNonNull(publicKey, "publicKey");
    Objects.requireNonNull(message, "message");
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
    return new Redacted("VerifyRequest")
        .hide("publicKey", publicKey)
        .hide("message", message)
        .hide("signature", signature)
        .toString();
  }
}
