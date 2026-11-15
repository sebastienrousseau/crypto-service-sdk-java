// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonValue;

/** A recipient kind for {@code POST /v2/multi-recipient/encrypt}. */
public enum RecipientType {
  /** {@code classical}. */
  CLASSICAL("classical"),
  /** {@code pq}. */
  PQ("pq");

  private final String wire;

  RecipientType(String wire) {
    this.wire = wire;
  }

  /**
   * The value the API uses.
   *
   * @return the wire name, for example {@code classical}
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
