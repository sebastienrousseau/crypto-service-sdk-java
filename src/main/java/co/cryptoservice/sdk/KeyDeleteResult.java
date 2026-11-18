// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * What {@code POST /v2/keys/delete} answers: delete a server-held key.
 *
 * @param deleted always true
 * @param keyId identifier of the server-held key; use it with the signing, decryption and
 *     decapsulation routes
 */
public record KeyDeleteResult(boolean deleted, String keyId) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyDeleteResult").show("deleted", deleted).show("keyId", keyId).toString();
  }
}
