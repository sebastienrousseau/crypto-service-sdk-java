// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/pq/slh-dsa/verify} answers: verify with SLH-DSA (FIPS 205).
 *
 * @param algorithm the algorithm
 * @param valid whether the signature is valid
 */
public record SlhDsaVerifyResult(String algorithm, boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("SlhDsaVerifyResult")
        .show("algorithm", algorithm)
        .show("valid", valid)
        .toString();
  }
}
