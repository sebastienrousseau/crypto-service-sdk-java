// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** A key-derivation function accepted by {@code POST /v2/kdf}. */
public enum KdfAlgorithm {
  /** {@code scrypt}. */
  SCRYPT("scrypt"),
  /** {@code hkdf-sha256}. */
  HKDF_SHA256("hkdf-sha256"),
  /** {@code pbkdf2-sha256}. */
  PBKDF2_SHA256("pbkdf2-sha256");

  private final String wire;

  KdfAlgorithm(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code scrypt}
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
