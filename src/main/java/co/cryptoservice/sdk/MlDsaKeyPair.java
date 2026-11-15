// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/dsa/keygen} answers: generate a server-held ML-DSA key pair (FIPS 204).
 *
 * @param algorithm the algorithm
 * @param keyId identifier of the server-held key; use it with the signing, decryption and
 *     decapsulation routes
 * @param publicKey the ML-DSA public key; lowercase hex
 */
public record MlDsaKeyPair(
    String algorithm,
    String keyId,
    String publicKey) {

  /**
   * {@link #publicKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code publicKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] publicKeyBytes() {
    return Bytes.fromHexOrNull(publicKey);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MlDsaKeyPair")
        .show("algorithm", algorithm)
        .show("keyId", keyId)
        .hide("publicKey", publicKey)
        .toString();
  }
}
