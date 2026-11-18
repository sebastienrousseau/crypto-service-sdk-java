// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/stream/iso20022} answers: verify ISO 20022 post-quantum dual-signature
 * envelope.
 *
 * @param classicalValid whether the Ed25519 signature verifies
 * @param digestMatches whether the payload digest matches the envelope
 * @param messageId the envelope's messageId
 * @param postQuantumValid whether the ML-DSA signature verifies
 * @param valid whether the digest and both signatures pass
 */
public record Iso20022VerifyResult(
    boolean classicalValid,
    boolean digestMatches,
    String messageId,
    boolean postQuantumValid,
    boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("Iso20022VerifyResult")
        .show("classicalValid", classicalValid)
        .show("digestMatches", digestMatches)
        .show("messageId", messageId)
        .show("postQuantumValid", postQuantumValid)
        .show("valid", valid)
        .toString();
  }
}
