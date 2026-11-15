// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/pq/dsa/verify} answers: verify with ML-DSA (FIPS 204).
 *
 * @param algorithm the algorithm
 * @param valid whether the signature is valid
 */
public record MlDsaVerifyResult(
    String algorithm,
    boolean valid) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MlDsaVerifyResult")
        .show("algorithm", algorithm)
        .show("valid", valid)
        .toString();
  }
}
