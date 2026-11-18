// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/opaque/login/init} answers: initiate OPAQUE login handshake.
 *
 * @param ke2 the serialized KE2 message; lowercase hex
 * @param sessionId single-use login session identifier; pass it to /v2/opaque/login/finish
 */
public record OpaqueLoginInitResult(String ke2, String sessionId) {

  /**
   * {@link #ke2() ke2()} decoded from hex.
   *
   * @return the bytes, or null when {@code ke2} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] ke2Bytes() {
    return Bytes.fromHexOrNull(ke2);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueLoginInitResult")
        .hide("ke2", ke2)
        .hide("sessionId", sessionId)
        .toString();
  }
}
