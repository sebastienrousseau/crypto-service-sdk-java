// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/keys/generate} answers: generate a server-held key pair for any supported
 * algorithm.
 *
 * @param algorithm the algorithm
 * @param keyId identifier of the server-held key; use it with the signing, decryption and
 *     decapsulation routes
 * @param kid key identifier: the request's metadata.kid, or base64url of the first 16 bytes of
 *     SHA-256(public key)
 * @param metadata key metadata: the request's, with kid always set
 * @param publicKey the public key; lowercase hex
 */
public record KeyGenerateResult(
    String algorithm,
    String keyId,
    String kid,
    KeyMetadata metadata,
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
    return new Redacted("KeyGenerateResult")
        .show("algorithm", algorithm)
        .show("keyId", keyId)
        .show("kid", kid)
        .hide("metadata", metadata)
        .hide("publicKey", publicKey)
        .toString();
  }
}
