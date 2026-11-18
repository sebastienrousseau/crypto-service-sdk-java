// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;

/**
 * The body of {@code POST /v2/stream/iso20022}.
 *
 * @param payload the signed payload
 * @param envelope the dual-signature envelope
 * @param trustedKeys the keys the signatures must verify under
 */
public record Iso20022VerifyRequest(
    JsonNode payload, Iso20022Envelope envelope, TrustedKeys trustedKeys) {

  /**
   * Checks that every required member is present.
   *
   * @throws NullPointerException when a required member is null
   */
  public Iso20022VerifyRequest {
    Objects.requireNonNull(payload, "payload");
    Objects.requireNonNull(envelope, "envelope");
    Objects.requireNonNull(trustedKeys, "trustedKeys");
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("Iso20022VerifyRequest")
        .hide("payload", payload)
        .hide("envelope", envelope)
        .hide("trustedKeys", trustedKeys)
        .toString();
  }
}
