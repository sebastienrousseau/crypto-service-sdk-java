// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/sealedbox/seal}.
 *
 * @param recipientPublicKey the recipient's public key as hex
 * @param plaintext the plaintext, as UTF-8 text
 */
public record SealedboxSealRequest(String recipientPublicKey, String plaintext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public SealedboxSealRequest {
    Objects.requireNonNull(recipientPublicKey, "recipientPublicKey");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SealedboxSealRequest")
        .hide("recipientPublicKey", recipientPublicKey)
        .hide("plaintext", plaintext)
        .toString();
  }
}
