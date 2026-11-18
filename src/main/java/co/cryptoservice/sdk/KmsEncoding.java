// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** How {@code POST /v2/kms/encrypt} reads its plaintext; never guessed from the content. */
public enum KmsEncoding {
  /** {@code utf8}. */
  UTF8("utf8"),
  /** {@code hex}. */
  HEX("hex");

  private final String wire;

  KmsEncoding(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code utf8}
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
