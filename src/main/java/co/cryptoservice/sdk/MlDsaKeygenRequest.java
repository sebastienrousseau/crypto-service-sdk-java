// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/pq/dsa/keygen}.
 *
 * @param level the security level
 */
public record MlDsaKeygenRequest(MlDsaLevel level) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public MlDsaKeygenRequest {
    Objects.requireNonNull(level, "level");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MlDsaKeygenRequest").show("level", level).toString();
  }
}
