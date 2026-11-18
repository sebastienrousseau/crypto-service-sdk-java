// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/encrypt}.
 *
 * @param key the 256-bit key as hex (64 characters)
 * @param plaintext the plaintext, as UTF-8 text
 */
public record EncryptRequest(String key, String plaintext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public EncryptRequest {
    Objects.requireNonNull(key, "key");
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
    return new Redacted("EncryptRequest").hide("key", key).hide("plaintext", plaintext).toString();
  }
}
