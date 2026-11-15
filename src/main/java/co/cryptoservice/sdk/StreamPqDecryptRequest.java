// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/stream/pq-decrypt}.
 *
 * @param keyId the server-held key's identifier
 * @param ciphertext the ciphertext, as the server returned it
 * @param chunkSize the stream chunk size in bytes, or null for the default
 */
public record StreamPqDecryptRequest(
    String keyId,
    String ciphertext,
    Integer chunkSize) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamPqDecryptRequest {
    Objects.requireNonNull(keyId, "keyId");
    Objects.requireNonNull(ciphertext, "ciphertext");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param keyId the server-held key's identifier
   * @param ciphertext the ciphertext, as the server returned it
   * @return the request
   */
  public static StreamPqDecryptRequest of(String keyId, String ciphertext) {
    return new StreamPqDecryptRequest(keyId, ciphertext, null);
  }

  /**
   * A copy with {@code chunkSize} replaced.
   *
   * @param chunkSize the stream chunk size in bytes, or null for the default
   * @return the new request
   */
  public StreamPqDecryptRequest withChunkSize(Integer chunkSize) {
    return new StreamPqDecryptRequest(keyId, ciphertext, chunkSize);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamPqDecryptRequest")
        .show("keyId", keyId)
        .hide("ciphertext", ciphertext)
        .show("chunkSize", chunkSize)
        .toString();
  }
}
