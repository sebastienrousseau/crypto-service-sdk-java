// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** An HPKE AEAD. */
public enum HpkeAead {
  /** {@code chacha20-poly1305}. */
  CHACHA20_POLY1305("chacha20-poly1305"),
  /** {@code aes-128-gcm}. */
  AES_128_GCM("aes-128-gcm");

  private final String wire;

  HpkeAead(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code chacha20-poly1305}
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
