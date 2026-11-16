// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;
import java.util.Objects;

/**
 * The body of {@code POST /v2/multi-recipient/encrypt}.
 *
 * @param plaintext the plaintext, as UTF-8 text
 * @param recipients the recipients
 */
public record MultiRecipientEncryptRequest(
    String plaintext,
    List<Recipient> recipients) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public MultiRecipientEncryptRequest {
    Objects.requireNonNull(plaintext, "plaintext");
    Objects.requireNonNull(recipients, "recipients");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MultiRecipientEncryptRequest")
        .hide("plaintext", plaintext)
        .hide("recipients", recipients)
        .toString();
  }
}
