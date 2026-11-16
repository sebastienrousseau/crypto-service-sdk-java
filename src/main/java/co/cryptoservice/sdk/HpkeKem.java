// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An HPKE key-encapsulation mechanism (RFC 9180, plus the X-Wing and X25519 + ML-KEM-768 hybrids).
 */
public enum HpkeKem {
  /** {@code x25519}. */
  X25519("x25519"),
  /** {@code p256}. */
  P256("p256"),
  /** {@code x-wing}. */
  X_WING("x-wing"),
  /** {@code x25519-ml-kem-768}. */
  X25519_ML_KEM_768("x25519-ml-kem-768");

  private final String wire;

  HpkeKem(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code x25519}
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
