// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;

/**
 * What {@code GET /v2/compliance/dora} answers: DORA cryptographic inventory (self-assessment).
 *
 * @param activePrimitivesCount algorithm identifiers listed
 * @param algorithmDeprecationSchedule the algorithmDeprecationSchedule
 * @param article the articles
 * @param cryptographicInventory the cryptographicInventory
 * @param disclaimer the disclaimer
 * @param postQuantumPrimitivesCount post-quantum identifiers listed
 * @param quantumResistanceRatio post-quantum identifiers / all identifiers, two decimals
 * @param standard the regulation
 * @param status the status
 * @param timestamp when the assessment was built
 */
public record DoraScorecard(
    int activePrimitivesCount,
    List<DoraDeprecation> algorithmDeprecationSchedule,
    String article,
    List<DoraInventoryItem> cryptographicInventory,
    String disclaimer,
    int postQuantumPrimitivesCount,
    double quantumResistanceRatio,
    String standard,
    String status,
    String timestamp) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("DoraScorecard")
        .show("activePrimitivesCount", activePrimitivesCount)
        .hide("algorithmDeprecationSchedule", algorithmDeprecationSchedule)
        .show("article", article)
        .hide("cryptographicInventory", cryptographicInventory)
        .hide("disclaimer", disclaimer)
        .show("postQuantumPrimitivesCount", postQuantumPrimitivesCount)
        .show("quantumResistanceRatio", quantumResistanceRatio)
        .show("standard", standard)
        .show("status", status)
        .show("timestamp", timestamp)
        .toString();
  }
}
