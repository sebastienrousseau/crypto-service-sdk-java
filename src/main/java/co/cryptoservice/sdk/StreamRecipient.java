// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * One element of {@code recipients}.
 *
 * @param mlKemPublicKey the ML-KEM public key as hex
 * @param recipientId the recipient's identifier
 * @param x25519PublicKey the X25519 public key as hex
 */
public record StreamRecipient(
    String mlKemPublicKey,
    String recipientId,
    String x25519PublicKey) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamRecipient {
    Objects.requireNonNull(mlKemPublicKey, "mlKemPublicKey");
    Objects.requireNonNull(recipientId, "recipientId");
    Objects.requireNonNull(x25519PublicKey, "x25519PublicKey");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamRecipient")
        .hide("mlKemPublicKey", mlKemPublicKey)
        .show("recipientId", recipientId)
        .hide("x25519PublicKey", x25519PublicKey)
        .toString();
  }
}
