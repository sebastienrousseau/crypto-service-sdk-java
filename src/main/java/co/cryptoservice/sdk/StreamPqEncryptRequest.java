// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/stream/pq-encrypt}.
 *
 * @param x25519PublicKey the X25519 public key as hex
 * @param mlKemPublicKey the ML-KEM public key as hex
 * @param plaintext the plaintext, as UTF-8 text
 * @param chunkSize the stream chunk size in bytes, or null for the default
 */
public record StreamPqEncryptRequest(
    String x25519PublicKey, String mlKemPublicKey, String plaintext, Integer chunkSize) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamPqEncryptRequest {
    Objects.requireNonNull(x25519PublicKey, "x25519PublicKey");
    Objects.requireNonNull(mlKemPublicKey, "mlKemPublicKey");
    Objects.requireNonNull(plaintext, "plaintext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param x25519PublicKey the X25519 public key as hex
   * @param mlKemPublicKey the ML-KEM public key as hex
   * @param plaintext the plaintext, as UTF-8 text
   * @return the request
   */
  public static StreamPqEncryptRequest of(
      String x25519PublicKey, String mlKemPublicKey, String plaintext) {
    return new StreamPqEncryptRequest(x25519PublicKey, mlKemPublicKey, plaintext, null);
  }

  /**
   * A copy with {@code chunkSize} replaced.
   *
   * @param chunkSize the stream chunk size in bytes, or null for the default
   * @return the new request
   */
  public StreamPqEncryptRequest withChunkSize(Integer chunkSize) {
    return new StreamPqEncryptRequest(x25519PublicKey, mlKemPublicKey, plaintext, chunkSize);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamPqEncryptRequest")
        .hide("x25519PublicKey", x25519PublicKey)
        .hide("mlKemPublicKey", mlKemPublicKey)
        .hide("plaintext", plaintext)
        .show("chunkSize", chunkSize)
        .toString();
  }
}
