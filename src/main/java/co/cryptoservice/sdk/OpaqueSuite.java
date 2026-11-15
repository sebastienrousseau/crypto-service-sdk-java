// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** An OPAQUE cipher suite. */
public enum OpaqueSuite {
  /** {@code P256-SHA256}. */
  P256_SHA256("P256-SHA256"),
  /** {@code ristretto255-SHA512}. */
  RISTRETTO255_SHA512("ristretto255-SHA512");

  private final String wire;

  OpaqueSuite(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code P256-SHA256}
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
