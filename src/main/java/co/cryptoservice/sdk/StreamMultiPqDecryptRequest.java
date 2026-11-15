// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Objects;

/**
 * The body of {@code POST /v2/stream/multi-pq-decrypt}.
 *
 * @param keyId the server-held key's identifier
 * @param ciphertext the ciphertext, as the server returned it
 * @param recipientId the recipient's identifier
 * @param chunkSize the stream chunk size in bytes, or null for the default
 */
public record StreamMultiPqDecryptRequest(
    String keyId,
    String ciphertext,
    String recipientId,
    Integer chunkSize) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamMultiPqDecryptRequest {
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
  public static StreamMultiPqDecryptRequest of(String keyId, String ciphertext) {
    return new StreamMultiPqDecryptRequest(keyId, ciphertext, null, null);
  }

  /**
   * A copy with {@code recipientId} replaced.
   *
   * @param recipientId the recipient's identifier
   * @return the new request
   */
  public StreamMultiPqDecryptRequest withRecipientId(String recipientId) {
    return new StreamMultiPqDecryptRequest(keyId, ciphertext, recipientId, chunkSize);
  }

  /**
   * A copy with {@code chunkSize} replaced.
   *
   * @param chunkSize the stream chunk size in bytes, or null for the default
   * @return the new request
   */
  public StreamMultiPqDecryptRequest withChunkSize(Integer chunkSize) {
    return new StreamMultiPqDecryptRequest(keyId, ciphertext, recipientId, chunkSize);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamMultiPqDecryptRequest")
        .show("keyId", keyId)
        .hide("ciphertext", ciphertext)
        .show("recipientId", recipientId)
        .show("chunkSize", chunkSize)
        .toString();
  }
}
