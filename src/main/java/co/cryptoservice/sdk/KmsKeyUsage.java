// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** What a KMS key is created for. */
public enum KmsKeyUsage {
  /** {@code encrypt}. */
  ENCRYPT("encrypt"),
  /** {@code sign}. */
  SIGN("sign"),
  /** {@code wrap}. */
  WRAP("wrap");

  private final String wire;

  KmsKeyUsage(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code encrypt}
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
