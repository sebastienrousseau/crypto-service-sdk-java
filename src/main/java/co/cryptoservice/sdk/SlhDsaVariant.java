// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** An SLH-DSA (FIPS 205) parameter set. */
public enum SlhDsaVariant {
  /** {@code sha2-128f}. */
  SHA2_128F("sha2-128f"),
  /** {@code sha2-128s}. */
  SHA2_128S("sha2-128s"),
  /** {@code sha2-192f}. */
  SHA2_192F("sha2-192f"),
  /** {@code sha2-192s}. */
  SHA2_192S("sha2-192s"),
  /** {@code sha2-256f}. */
  SHA2_256F("sha2-256f"),
  /** {@code sha2-256s}. */
  SHA2_256S("sha2-256s"),
  /** {@code shake-128f}. */
  SHAKE_128F("shake-128f"),
  /** {@code shake-128s}. */
  SHAKE_128S("shake-128s"),
  /** {@code shake-192f}. */
  SHAKE_192F("shake-192f"),
  /** {@code shake-192s}. */
  SHAKE_192S("shake-192s"),
  /** {@code shake-256f}. */
  SHAKE_256F("shake-256f"),
  /** {@code shake-256s}. */
  SHAKE_256S("shake-256s");

  private final String wire;

  SlhDsaVariant(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code sha2-128f}
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
