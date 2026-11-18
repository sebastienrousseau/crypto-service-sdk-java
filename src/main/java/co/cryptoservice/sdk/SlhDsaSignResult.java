// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/slh-dsa/sign} answers: sign with SLH-DSA (FIPS 205).
 *
 * @param algorithm the algorithm
 * @param signature the SLH-DSA signature; lowercase hex
 */
public record SlhDsaSignResult(String algorithm, String signature) {

  /**
   * {@link #signature() signature()} decoded from hex.
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
    return new Redacted("SlhDsaSignResult")
        .show("algorithm", algorithm)
        .hide("signature", signature)
        .toString();
  }
}
