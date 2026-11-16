// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/hpke/seal}.
 *
 * @param recipientPublicKey the recipient's public key as hex
 * @param plaintext the plaintext, as UTF-8 text
 * @param kem the KEM, or null for the server default
 * @param aead the AEAD, or null for the server default
 * @param info the HPKE info, or null
 * @param aad additional authenticated data, or null
 * @param psk the pre-shared key, or null
 * @param pskId the pre-shared key identifier, or null
 */
public record HpkeSealRequest(
    String recipientPublicKey,
    String plaintext,
    HpkeKem kem,
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
  public HpkeSealRequest {
    Objects.requireNonNull(recipientPublicKey, "recipientPublicKey");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param recipientPublicKey the recipient's public key as hex
   * @param plaintext the plaintext, as UTF-8 text
   * @return the request
   */
  public static HpkeSealRequest of(String recipientPublicKey, String plaintext) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, null, null, null, null, null, null);
  }

  /**
   * A copy with {@code kem} replaced.
   *
   * @param kem the KEM, or null for the server default
   * @return the new request
   */
  public HpkeSealRequest withKem(HpkeKem kem) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, kem, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code aead} replaced.
   *
   * @param aead the AEAD, or null for the server default
   * @return the new request
   */
  public HpkeSealRequest withAead(HpkeAead aead) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, kem, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code info} replaced.
   *
   * @param info the HPKE info, or null
   * @return the new request
   */
  public HpkeSealRequest withInfo(String info) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, kem, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code aad} replaced.
   *
   * @param aad additional authenticated data, or null
   * @return the new request
   */
  public HpkeSealRequest withAad(String aad) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, kem, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code psk} replaced.
   *
   * @param psk the pre-shared key, or null
   * @return the new request
   */
  public HpkeSealRequest withPsk(String psk) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, kem, aead, info, aad, psk, pskId);
  }

  /**
   * A copy with {@code pskId} replaced.
   *
   * @param pskId the pre-shared key identifier, or null
   * @return the new request
   */
  public HpkeSealRequest withPskId(String pskId) {
    return new HpkeSealRequest(recipientPublicKey, plaintext, kem, aead, info, aad, psk, pskId);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HpkeSealRequest")
        .hide("recipientPublicKey", recipientPublicKey)
        .hide("plaintext", plaintext)
        .show("kem", kem)
        .show("aead", aead)
        .hide("info", info)
        .hide("aad", aad)
        .hide("psk", psk)
        .hide("pskId", pskId)
        .toString();
  }
}
