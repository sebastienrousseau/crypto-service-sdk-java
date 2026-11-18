// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * The body of {@code POST /v2/hpke/keygen}.
 *
 * @param kem the KEM, or null for the server default
 */
public record HpkeKeygenRequest(HpkeKem kem) {

  /**
   * A copy with {@code kem} replaced.
   *
   * @param kem the KEM, or null for the server default
   * @return the new request
   */
  public HpkeKeygenRequest withKem(HpkeKem kem) {
    return new HpkeKeygenRequest(kem);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HpkeKeygenRequest").show("kem", kem).toString();
  }
}
