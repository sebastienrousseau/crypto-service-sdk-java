// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/verify} answers: verify an Ed25519 signature.
 *
 * @param algorithm signature algorithm
 * @param valid whether the signature is valid
 */
public record VerifyResult(String algorithm, boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("VerifyResult")
        .show("algorithm", algorithm)
        .show("valid", valid)
        .toString();
  }
}
