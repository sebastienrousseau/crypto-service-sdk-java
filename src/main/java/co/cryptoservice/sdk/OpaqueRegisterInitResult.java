// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/opaque/register/init} answers: initiate OPAQUE credential registration.
 *
 * @param response the serialized RegistrationResponse; lowercase hex
 */
public record OpaqueRegisterInitResult(String response) {

  /**
   * {@link #response() response()} decoded from hex.
   *
   * @return the bytes, or null when {@code response} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] responseBytes() {
    return Bytes.fromHexOrNull(response);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("OpaqueRegisterInitResult").hide("response", response).toString();
  }
}
