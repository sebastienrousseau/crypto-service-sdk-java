// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/dsa/sign} answers: sign with ML-DSA (FIPS 204).
 *
 * @param algorithm the algorithm
 * @param signature the ML-DSA signature; lowercase hex
 */
public record MlDsaSignResult(String algorithm, String signature) {

  /**
   * {@link #signature()} decoded from hex.
   *
   * @return the bytes, or null when {@code signature} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] signatureBytes() {
    return Bytes.fromHexOrNull(signature);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MlDsaSignResult")
        .show("algorithm", algorithm)
        .hide("signature", signature)
        .toString();
  }
}
