// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/opaque/login/finish} answers: finish OPAQUE login handshake.
 *
 * @param sessionKey the session key; lowercase hex
 */
public record OpaqueLoginFinishResult(String sessionKey) {

  /**
   * {@link #sessionKey() sessionKey()} decoded from hex.
   *
   * @return the bytes, or null when {@code sessionKey} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] sessionKeyBytes() {
    return Bytes.fromHexOrNull(sessionKey);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueLoginFinishResult").hide("sessionKey", sessionKey).toString();
  }
}
