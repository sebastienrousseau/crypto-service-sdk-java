// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * One element of {@code signatures}.
 *
 * @param algorithm signature algorithm
 * @param id the item's id
 * @param signature the 64-byte Ed25519 signature; lowercase hex
 */
public record StreamSignature(String algorithm, String id, String signature) {

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
    return new Redacted("StreamSignature")
        .show("algorithm", algorithm)
        .show("id", id)
        .hide("signature", signature)
        .toString();
  }
}
