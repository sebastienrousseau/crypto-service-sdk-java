// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** An ML-DSA (FIPS 204) security level. */
public enum MlDsaLevel {
  /** Level 44. */
  ML_DSA_44(44),
  /** Level 65. */
  ML_DSA_65(65),
  /** Level 87. */
  ML_DSA_87(87);

  private final int wire;

  MlDsaLevel(int wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the level number, for example {@code 44}
   */
  @JsonValue
  public int wire() {
    return wire;
  }
}
