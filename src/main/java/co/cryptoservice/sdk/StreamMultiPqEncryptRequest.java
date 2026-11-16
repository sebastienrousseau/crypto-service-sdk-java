// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;
import java.util.Objects;

/**
 * The body of {@code POST /v2/stream/multi-pq-encrypt}.
 *
 * @param plaintext the plaintext, as UTF-8 text
 * @param recipients the recipients
 * @param chunkSize the stream chunk size in bytes, or null for the default
 */
public record StreamMultiPqEncryptRequest(
    String plaintext,
    List<StreamRecipient> recipients,
    Integer chunkSize) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public StreamMultiPqEncryptRequest {
    Objects.requireNonNull(plaintext, "plaintext");
    Objects.requireNonNull(recipients, "recipients");
  }

  /**
   * A request with only the required members; the optional ones are null.
   *
   * @param plaintext the plaintext, as UTF-8 text
   * @param recipients the recipients
   * @return the request
   */
  public static StreamMultiPqEncryptRequest of(String plaintext, List<StreamRecipient> recipients) {
    return new StreamMultiPqEncryptRequest(plaintext, recipients, null);
  }

  /**
   * A copy with {@code chunkSize} replaced.
   *
   * @param chunkSize the stream chunk size in bytes, or null for the default
   * @return the new request
   */
  public StreamMultiPqEncryptRequest withChunkSize(Integer chunkSize) {
    return new StreamMultiPqEncryptRequest(plaintext, recipients, chunkSize);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamMultiPqEncryptRequest")
        .hide("plaintext", plaintext)
        .hide("recipients", recipients)
        .show("chunkSize", chunkSize)
        .toString();
  }
}
