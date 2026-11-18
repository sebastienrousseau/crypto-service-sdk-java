// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.List;
import java.util.Objects;

/**
 * The body of {@code POST /v2/stream/sign}.
 *
 * @param items the items
 */
public record StreamSignRequest(List<StreamSignItem> items) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamSignRequest {
    Objects.requireNonNull(items, "items");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamSignRequest").hide("items", items).toString();
  }
}
