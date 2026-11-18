// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/keys/export} answers: export a server-held private key.
 *
 * @param algorithm algorithm of the key, e.g. ed25519, x25519, ml-kem-768, x25519-ml-kem-768,
 *     ml-dsa-65, slh-dsa-sha2-128f
 * @param keyId identifier of the server-held key; use it with the signing, decryption and
 *     decapsulation routes
 * @param mlKemPublicKey ML-KEM-768 public key (x25519-ml-kem-768); lowercase hex, or null when
 *     absent
 * @param mlKemSecretKey ML-KEM-768 secret key (x25519-ml-kem-768); lowercase hex, or null when
 *     absent
 * @param privateKey private key (POST /v2/keys/generate and POST /v2/hpke/keygen); lowercase hex,
 *     or null when absent
 * @param publicKey public key (single-key algorithms); lowercase hex, or null when absent
 * @param secretKey secret key (POST /v2/pq/keygen, /v2/pq/dsa/keygen, /v2/pq/slh-dsa/keygen);
 *     lowercase hex, or null when absent
 * @param x25519PrivateKey X25519 private key (x25519-ml-kem-768); lowercase hex, or null when
 *     absent
 * @param x25519PublicKey X25519 public key (x25519-ml-kem-768); lowercase hex, or null when absent
 */
public record KeyExportResult(
    String algorithm,
    String keyId,
    String mlKemPublicKey,
    String mlKemSecretKey,
    String privateKey,
    String publicKey,
    String secretKey,
    String x25519PrivateKey,
    String x25519PublicKey) {

  /**
   * {@link #mlKemPublicKey() mlKemPublicKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code mlKemPublicKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] mlKemPublicKeyBytes() {
    return Bytes.fromHexOrNull(mlKemPublicKey);
  }

  /**
   * {@link #mlKemSecretKey() mlKemSecretKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code mlKemSecretKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] mlKemSecretKeyBytes() {
    return Bytes.fromHexOrNull(mlKemSecretKey);
  }

  /**
   * {@link #privateKey() privateKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code privateKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] privateKeyBytes() {
    return Bytes.fromHexOrNull(privateKey);
  }

  /**
   * {@link #publicKey() publicKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code publicKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] publicKeyBytes() {
    return Bytes.fromHexOrNull(publicKey);
  }

  /**
   * {@link #secretKey() secretKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code secretKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] secretKeyBytes() {
    return Bytes.fromHexOrNull(secretKey);
  }

  /**
   * {@link #x25519PrivateKey() x25519PrivateKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code x25519PrivateKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] x25519PrivateKeyBytes() {
    return Bytes.fromHexOrNull(x25519PrivateKey);
  }

  /**
   * {@link #x25519PublicKey() x25519PublicKey()} decoded from hex.
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
    return new Redacted("KeyExportResult")
        .show("algorithm", algorithm)
        .show("keyId", keyId)
        .hide("mlKemPublicKey", mlKemPublicKey)
        .hide("mlKemSecretKey", mlKemSecretKey)
        .hide("privateKey", privateKey)
        .hide("publicKey", publicKey)
        .hide("secretKey", secretKey)
        .hide("x25519PrivateKey", x25519PrivateKey)
        .hide("x25519PublicKey", x25519PublicKey)
        .toString();
  }
}
