// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/sign} answers: sign with Ed25519.
 *
 * @param algorithm signature algorithm
 * @param signature the 64-byte Ed25519 signature; lowercase hex
 */
public record SignResult(
    String algorithm,
    String signature) {

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
    return new Redacted("SignResult")
        .show("algorithm", algorithm)
        .hide("signature", signature)
        .toString();
  }
}
