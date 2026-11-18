// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** A hash algorithm accepted by {@code POST /v2/hash}. */
public enum HashAlgorithm {
  /** {@code sha256}. */
  SHA256("sha256"),
  /** {@code sha384}. */
  SHA384("sha384"),
  /** {@code sha512}. */
  SHA512("sha512"),
  /** {@code sha3-256}. */
  SHA3_256("sha3-256"),
  /** {@code sha3-512}. */
  SHA3_512("sha3-512"),
  /** {@code blake2b}. */
  BLAKE2B("blake2b"),
  /** {@code blake3}. */
  BLAKE3("blake3");

  private final String wire;

  HashAlgorithm(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code sha256}
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
