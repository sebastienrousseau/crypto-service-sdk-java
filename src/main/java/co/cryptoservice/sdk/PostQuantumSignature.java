// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The {@code postQuantum} member.
 *
 * @param algorithm the algorithm
 * @param publicKey the public key as hex
 * @param signature the signature as hex
 */
public record PostQuantumSignature(String algorithm, String publicKey, String signature) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public PostQuantumSignature {
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
    return new Redacted("PostQuantumSignature")
        .show("algorithm", algorithm)
        .hide("publicKey", publicKey)
        .hide("signature", signature)
        .toString();
  }
}
