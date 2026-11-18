// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** A key-pair algorithm accepted by {@code POST /v2/keys/generate}. */
public enum KeyAlgorithm {
  /** {@code ed25519}. */
  ED25519("ed25519"),
  /** {@code x25519}. */
  X25519("x25519"),
  /** {@code ed448}. */
  ED448("ed448"),
  /** {@code x448}. */
  X448("x448"),
  /** {@code p256}. */
  P256("p256"),
  /** {@code p384}. */
  P384("p384"),
  /** {@code ml-kem-512}. */
  ML_KEM_512("ml-kem-512"),
  /** {@code ml-kem-768}. */
  ML_KEM_768("ml-kem-768"),
  /** {@code ml-kem-1024}. */
  ML_KEM_1024("ml-kem-1024"),
  /** {@code ml-dsa-44}. */
  ML_DSA_44("ml-dsa-44"),
  /** {@code ml-dsa-65}. */
  ML_DSA_65("ml-dsa-65"),
  /** {@code ml-dsa-87}. */
  ML_DSA_87("ml-dsa-87");

  private final String wire;

  KeyAlgorithm(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code ed25519}
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
