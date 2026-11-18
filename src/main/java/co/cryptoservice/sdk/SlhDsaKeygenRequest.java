// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/pq/slh-dsa/keygen}.
 *
 * @param variant the parameter set
 */
public record SlhDsaKeygenRequest(SlhDsaVariant variant) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public SlhDsaKeygenRequest {
    Objects.requireNonNull(variant, "variant");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SlhDsaKeygenRequest").show("variant", variant).toString();
  }
}
