// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * One element of {@code recipients}.
 *
 * @param ephemeralPublicKey the sender's ephemeral X25519 public key; lowercase hex
 * @param type x25519 for a classical recipient, x25519-ml-kem-768 for a pq one
 * @param wrappedKey the content key, wrapped for this recipient; standard base64 (RFC 4648 section
 *     4), padded
 * @param mlKemCiphertext the ML-KEM-768 ciphertext (pq recipients only); lowercase hex, or null
 *     when absent
 */
public record RecipientEnvelope(
    String ephemeralPublicKey,
    String type,
    String wrappedKey,
    String mlKemCiphertext) {

  /**
   * {@link #ephemeralPublicKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code ephemeralPublicKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] ephemeralPublicKeyBytes() {
    return Bytes.fromHexOrNull(ephemeralPublicKey);
  }

  /**
   * {@link #wrappedKey()} decoded from base64.
   *
   * @return the bytes, or null when {@code wrappedKey} is absent
   * @throws IllegalArgumentException when the server sent malformed base64
   */
  @JsonIgnore
  public byte[] wrappedKeyBytes() {
    return Bytes.fromBase64OrNull(wrappedKey);
  }

  /**
   * {@link #mlKemCiphertext()} decoded from hex.
   *
   * @return the bytes, or null when {@code mlKemCiphertext} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] mlKemCiphertextBytes() {
    return Bytes.fromHexOrNull(mlKemCiphertext);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("RecipientEnvelope")
        .hide("ephemeralPublicKey", ephemeralPublicKey)
        .show("type", type)
        .hide("wrappedKey", wrappedKey)
        .hide("mlKemCiphertext", mlKemCiphertext)
        .toString();
  }
}
