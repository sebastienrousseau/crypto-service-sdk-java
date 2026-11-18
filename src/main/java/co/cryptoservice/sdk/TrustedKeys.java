// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The {@code trustedKeys} member.
 *
 * @param classicalPublicKey the trusted classical public key as hex
 * @param postQuantumPublicKey the trusted post-quantum public key as hex
 */
public record TrustedKeys(String classicalPublicKey, String postQuantumPublicKey) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public TrustedKeys {
    Objects.requireNonNull(classicalPublicKey, "classicalPublicKey");
    Objects.requireNonNull(postQuantumPublicKey, "postQuantumPublicKey");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("TrustedKeys")
        .hide("classicalPublicKey", classicalPublicKey)
        .hide("postQuantumPublicKey", postQuantumPublicKey)
        .toString();
  }
}
