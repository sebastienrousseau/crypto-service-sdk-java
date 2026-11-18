// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * What {@code POST /v2/hash} answers: compute a cryptographic hash.
 *
 * @param algorithm the algorithm
 * @param digest the digest; lowercase hex
 * @param length digest length in bytes
 */
public record HashResult(String algorithm, String digest, int length) {

  /**
   * {@link #digest() digest()} decoded from hex.
   *
   * @return the bytes, or null when {@code digest} is absent
   * @throws IllegalArgumentException when the server sent malformed hex
   */
  @JsonIgnore
  public byte[] digestBytes() {
    return Bytes.fromHexOrNull(digest);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("HashResult")
        .show("algorithm", algorithm)
        .hide("digest", digest)
        .show("length", length)
        .toString();
  }
}
