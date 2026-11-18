// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/hmac} answers: compute an HMAC.
 *
 * @param algorithm the algorithm
 * @param mac the tag; lowercase hex
 */
public record MacResult(String algorithm, String mac) {

  /**
   * {@link #mac()} decoded from hex.
   *
   * @return the bytes, or null when {@code mac} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] macBytes() {
    return Bytes.fromHexOrNull(mac);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("MacResult").show("algorithm", algorithm).hide("mac", mac).toString();
  }
}
