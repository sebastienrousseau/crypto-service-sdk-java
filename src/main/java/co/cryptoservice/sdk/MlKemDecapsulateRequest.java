// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/pq/decapsulate}.
 *
 * @param keyId the server-held key's identifier
 * @param ciphertext the ciphertext, as the server returned it
 */
public record MlKemDecapsulateRequest(String keyId, String ciphertext) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public MlKemDecapsulateRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MlKemDecapsulateRequest")
        .show("keyId", keyId)
        .hide("ciphertext", ciphertext)
        .toString();
  }
}
