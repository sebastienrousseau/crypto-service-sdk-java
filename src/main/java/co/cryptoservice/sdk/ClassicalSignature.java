// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The {@code classical} member.
 *
 * @param algorithm the algorithm
 * @param publicKey the public key as hex
 * @param signature the signature as hex
 */
public record ClassicalSignature(String algorithm, String publicKey, String signature) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public ClassicalSignature {
    Objects.requireNonNull(algorithm, "algorithm");
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
    return new Redacted("ClassicalSignature")
        .show("algorithm", algorithm)
        .hide("publicKey", publicKey)
        .hide("signature", signature)
        .toString();
  }
}
