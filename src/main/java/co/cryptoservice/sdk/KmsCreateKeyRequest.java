// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.Map;

/**
 * The body of {@code POST /v2/kms/create-key}.
 *
 * @param provider the KMS provider, or null for the default
 * @param algorithm the algorithm
 * @param usage the key usage, or null for {@code encrypt}
 * @param metadata the key metadata, or null
 */
public record KmsCreateKeyRequest(
    String provider, String algorithm, KmsKeyUsage usage, Map<String, String> metadata) {

  /**
   * A copy with {@code provider} replaced.
   *
   * @param provider the KMS provider, or null for the default
   * @return the new request
   */
  public KmsCreateKeyRequest withProvider(String provider) {
    return new KmsCreateKeyRequest(provider, algorithm, usage, metadata);
  }

  /**
   * A copy with {@code algorithm} replaced.
   *
   * @param algorithm the algorithm
   * @return the new request
   */
  public KmsCreateKeyRequest withAlgorithm(String algorithm) {
    return new KmsCreateKeyRequest(provider, algorithm, usage, metadata);
  }

  /**
   * A copy with {@code usage} replaced.
   *
   * @param usage the key usage, or null for {@code encrypt}
   * @return the new request
   */
  public KmsCreateKeyRequest withUsage(KmsKeyUsage usage) {
    return new KmsCreateKeyRequest(provider, algorithm, usage, metadata);
  }

  /**
   * A copy with {@code metadata} replaced.
   *
   * @param metadata the key metadata, or null
   * @return the new request
   */
  public KmsCreateKeyRequest withMetadata(Map<String, String> metadata) {
    return new KmsCreateKeyRequest(provider, algorithm, usage, metadata);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KmsCreateKeyRequest")
        .show("provider", provider)
        .show("algorithm", algorithm)
        .show("usage", usage)
        .hide("metadata", metadata)
        .toString();
  }
}
