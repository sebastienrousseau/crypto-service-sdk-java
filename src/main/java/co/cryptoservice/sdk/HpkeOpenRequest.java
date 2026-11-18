// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/hpke/open}.
 *
 * @param keyId the server-held key's identifier
 * @param encapsulatedKey the HPKE encapsulated key as hex
 * @param ciphertext the ciphertext, as the server returned it
 * @param aead the AEAD, or null for the server default
 * @param info the HPKE info, or null
 * @param aad additional authenticated data, or null
 * @param psk the pre-shared key, or null
 * @param pskId the pre-shared key identifier, or null
 */
public record HpkeOpenRequest(
    String keyId,
    String encapsulatedKey,
    String ciphertext,
    HpkeAead aead,
    String info,
    String aad,
    String psk,
    String pskId) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public HpkeOpenRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(encapsulatedKey, "encapsulatedKey");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @param encapsulatedKey the HPKE encapsulated key as hex
   * @param ciphertext the ciphertext, as the server returned it
   * @return the request
   */
  public static HpkeOpenRequest of(String keyId, String encapsulatedKey, String ciphertext) {
    return new HpkeOpenRequest(keyId, encapsulatedKey, ciphertext, null, null, null, null, null);
  }

  /**
   * A copy with {@code aead} replaced.
   *
   * @param aead the AEAD, or null for the server default
   * @return the new request
   */
  public HpkeOpenRequest withAead(HpkeAead aead) {
    return new HpkeOpenRequest(keyId, encapsulatedKey, ciphertext, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code info} replaced.
   *
   * @param info the HPKE info, or null
   * @return the new request
   */
  public HpkeOpenRequest withInfo(String info) {
    return new HpkeOpenRequest(keyId, encapsulatedKey, ciphertext, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code aad} replaced.
   *
   * @param aad additional authenticated data, or null
   * @return the new request
   */
  public HpkeOpenRequest withAad(String aad) {
    return new HpkeOpenRequest(keyId, encapsulatedKey, ciphertext, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code psk} replaced.
   *
   * @param psk the pre-shared key, or null
   * @return the new request
   */
  public HpkeOpenRequest withPsk(String psk) {
    return new HpkeOpenRequest(keyId, encapsulatedKey, ciphertext, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code pskId} replaced.
   *
   * @param pskId the pre-shared key identifier, or null
   * @return the new request
   */
  public HpkeOpenRequest withPskId(String pskId) {
    return new HpkeOpenRequest(keyId, encapsulatedKey, ciphertext, aead, info, aad, psk, pskId);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HpkeOpenRequest")
        .show("keyId", keyId)
        .hide("encapsulatedKey", encapsulatedKey)
        .hide("ciphertext", ciphertext)
        .show("aead", aead)
        .hide("info", info)
        .hide("aad", aad)
        .hide("psk", psk)
        .hide("pskId", pskId)
        .toString();
  }
}
