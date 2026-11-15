// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * One element of {@code recipients}.
 *
 * @param publicKey the public key as hex
 * @param type the recipient type
 * @param mlKemPublicKey the ML-KEM public key as hex
 */
public record Recipient(
    String publicKey,
    RecipientType type,
    String mlKemPublicKey) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public Recipient {
    Objects.requireNonNull(publicKey, "publicKey");
    Objects.requireNonNull(type, "type");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param publicKey the public key as hex
   * @param type the recipient type
   * @return the request
   */
  public static Recipient of(String publicKey, RecipientType type) {
    return new Recipient(publicKey, type, null);
  }

  /**
   * A copy with {@code mlKemPublicKey} replaced.
   *
   * @param mlKemPublicKey the ML-KEM public key as hex
   * @return the new request
   */
  public Recipient withMlKemPublicKey(String mlKemPublicKey) {
    return new Recipient(publicKey, type, mlKemPublicKey);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("Recipient")
        .hide("publicKey", publicKey)
        .show("type", type)
        .hide("mlKemPublicKey", mlKemPublicKey)
        .toString();
  }
}
