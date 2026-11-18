// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * The body of {@code POST /v2/keys/generate}.
 *
 * @param algorithm the algorithm
 * @param metadata the key metadata, or null
 */
public record KeyGenerateRequest(KeyAlgorithm algorithm, KeyMetadata metadata) {

  /**
   * A copy with {@code algorithm} replaced.
   *
   * @param algorithm the algorithm
   * @return the new request
   */
  public KeyGenerateRequest withAlgorithm(KeyAlgorithm algorithm) {
    return new KeyGenerateRequest(algorithm, metadata);
  }

  /**
   * A copy with {@code metadata} replaced.
   *
   * @param metadata the key metadata, or null
   * @return the new request
   */
  public KeyGenerateRequest withMetadata(KeyMetadata metadata) {
    return new KeyGenerateRequest(algorithm, metadata);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyGenerateRequest")
        .show("algorithm", algorithm)
        .hide("metadata", metadata)
        .toString();
  }
}
