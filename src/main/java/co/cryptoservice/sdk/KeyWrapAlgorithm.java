// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** An AES key-wrap mode accepted by {@code POST /v2/keys/wrap} and {@code /v2/keys/unwrap}. */
public enum KeyWrapAlgorithm {
  /** {@code aes-kw}. */
  AES_KW("aes-kw"),
  /** {@code aes-kwp}. */
  AES_KWP("aes-kwp");

  private final String wire;

  KeyWrapAlgorithm(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code aes-kw}
   */
  @JsonValue
  public String wire() {
    return wire;
  }

  @Override
  public String toString() {
    return wire;
  }
}
