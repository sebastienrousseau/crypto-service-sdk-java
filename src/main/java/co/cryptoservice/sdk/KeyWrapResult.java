// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/keys/wrap} answers: wrap a key using AES-KW or AES-KWP (RFC 3394/5649).
 *
 * @param algorithm the algorithm
 * @param wrapped the wrapped key (RFC 3394 / RFC 5649 output); standard base64 (RFC 4648 section
 *     4), padded
 */
public record KeyWrapResult(
    String algorithm,
    String wrapped) {

  /**
   * {@link #wrapped()} decoded from base64.
   *
   * @return the bytes, or null when {@code wrapped} is absent
   * @throws IllegalArgumentException when the server sent malformed base64
   */
  @JsonIgnore
  public byte[] wrappedBytes() {
    return Bytes.fromBase64OrNull(wrapped);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyWrapResult")
        .show("algorithm", algorithm)
        .hide("wrapped", wrapped)
        .toString();
  }
}
