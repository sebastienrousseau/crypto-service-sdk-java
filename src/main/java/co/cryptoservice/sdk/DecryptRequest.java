// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/decrypt}.
 *
 * @param key the 256-bit key as hex (64 characters)
 * @param ciphertext the ciphertext, as the server returned it
 */
public record DecryptRequest(
    String key,
    String ciphertext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public DecryptRequest {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("DecryptRequest")
        .hide("key", key)
        .hide("ciphertext", ciphertext)
        .toString();
  }
}
