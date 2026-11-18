// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * What {@code POST /v2/multi-recipient/encrypt} answers: encrypt for multiple recipients (hybrid
 * key wrapping).
 *
 * @param algorithm envelope algorithm
 * @param ciphertext the content, sealed with XChaCha20-Poly1305 under a random key; standard base64
 *     (RFC 4648 section 4), padded
 * @param recipients the recipients
 */
public record MultiRecipientResult(
    String algorithm, String ciphertext, List<RecipientEnvelope> recipients) {

  /**
   * {@link #ciphertext()} decoded from base64.
   *
   * @return the bytes, or null when {@code ciphertext} is absent
   * @throws IllegalArgumentException when the server sent malformed base64
   */
  @JsonIgnore
  public byte[] ciphertextBytes() {
    return Bytes.fromBase64OrNull(ciphertext);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MultiRecipientResult")
        .show("algorithm", algorithm)
        .hide("ciphertext", ciphertext)
        .hide("recipients", recipients)
        .toString();
  }
}
