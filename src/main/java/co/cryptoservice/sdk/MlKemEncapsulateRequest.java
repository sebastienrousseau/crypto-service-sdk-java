// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/pq/encapsulate}.
 *
 * @param publicKey the public key as hex
 */
public record MlKemEncapsulateRequest(String publicKey) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public MlKemEncapsulateRequest {
    Objects.requireNonNull(publicKey, "publicKey");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MlKemEncapsulateRequest").hide("publicKey", publicKey).toString();
  }
}
