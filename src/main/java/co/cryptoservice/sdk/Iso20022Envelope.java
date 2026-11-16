// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The {@code envelope} member.
 *
 * @param classical the classical signature
 * @param digestAlgorithm the payload digest algorithm
 * @param messageId the message identifier
 * @param messageType the ISO 20022 message type
 * @param payloadDigest the payload digest as hex
 * @param postQuantum the post-quantum signature
 * @param timestamp the signing time
 */
public record Iso20022Envelope(
    ClassicalSignature classical,
    String digestAlgorithm,
    String messageId,
    String messageType,
    String payloadDigest,
    PostQuantumSignature postQuantum,
    String timestamp) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public Iso20022Envelope {
    Objects.requireNonNull(classical, "classical");
    Objects.requireNonNull(digestAlgorithm, "digestAlgorithm");
    Objects.requireNonNull(messageId, "messageId");
    Objects.requireNonNull(messageType, "messageType");
    Objects.requireNonNull(payloadDigest, "payloadDigest");
    Objects.requireNonNull(postQuantum, "postQuantum");
    Objects.requireNonNull(timestamp, "timestamp");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("Iso20022Envelope")
        .hide("classical", classical)
        .show("digestAlgorithm", digestAlgorithm)
        .show("messageId", messageId)
        .show("messageType", messageType)
        .hide("payloadDigest", payloadDigest)
        .hide("postQuantum", postQuantum)
        .show("timestamp", timestamp)
        .toString();
  }
}
