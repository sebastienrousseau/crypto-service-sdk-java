// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/keys/wrap}.
 *
 * @param kek the key-encryption key as hex
 * @param keyToWrap the key to wrap, as hex
 * @param algorithm the algorithm
 */
public record KeyWrapRequest(
    String kek,
    String keyToWrap,
    KeyWrapAlgorithm algorithm) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public KeyWrapRequest {
    Objects.requireNonNull(kek, "kek");
    Objects.requireNonNull(keyToWrap, "keyToWrap");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param kek the key-encryption key as hex
   * @param keyToWrap the key to wrap, as hex
   * @return the request
   */
  public static KeyWrapRequest of(String kek, String keyToWrap) {
    return new KeyWrapRequest(kek, keyToWrap, null);
  }

  /**
   * A copy with {@code algorithm} replaced.
   *
   * @param algorithm the algorithm
   * @return the new request
   */
  public KeyWrapRequest withAlgorithm(KeyWrapAlgorithm algorithm) {
    return new KeyWrapRequest(kek, keyToWrap, algorithm);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyWrapRequest")
        .hide("kek", kek)
        .hide("keyToWrap", keyToWrap)
        .show("algorithm", algorithm)
        .toString();
  }
}
