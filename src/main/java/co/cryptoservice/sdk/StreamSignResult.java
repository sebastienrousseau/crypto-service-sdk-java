// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;

/**
 * What {@code POST /v2/stream/sign} answers: batch sign messages with sub-millisecond throughput.
 *
 * @param count number of signatures
 * @param signatures the signatures
 */
public record StreamSignResult(int count, List<StreamSignature> signatures) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("StreamSignResult")
        .show("count", count)
        .hide("signatures", signatures)
        .toString();
  }
}
