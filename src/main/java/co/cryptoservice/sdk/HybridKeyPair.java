// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/pq/hybrid/keygen} answers: generate a server-held hybrid X25519 + ML-KEM-768
 * key pair.
 *
 * @param algorithm hybrid KEM algorithm
 * @param keyId identifier of the server-held key; use it with the signing, decryption and
 *     decapsulation routes
 * @param mlKemPublicKey the 1184-byte ML-KEM-768 public key; lowercase hex
 * @param x25519PublicKey the 32-byte X25519 public key; lowercase hex
 */
public record HybridKeyPair(
    String algorithm, String keyId, String mlKemPublicKey, String x25519PublicKey) {

  /**
   * {@link #mlKemPublicKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code mlKemPublicKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] mlKemPublicKeyBytes() {
    return Bytes.fromHexOrNull(mlKemPublicKey);
  }

  /**
   * {@link #x25519PublicKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code x25519PublicKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] x25519PublicKeyBytes() {
    return Bytes.fromHexOrNull(x25519PublicKey);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HybridKeyPair")
        .show("algorithm", algorithm)
        .show("keyId", keyId)
        .hide("mlKemPublicKey", mlKemPublicKey)
        .hide("x25519PublicKey", x25519PublicKey)
        .toString();
  }
}
