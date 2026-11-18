// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * One element of {@code algorithmDeprecationSchedule}.
 *
 * @param algorithm the algorithm
 * @param category the category
 * @param recommendedMigration the recommendedMigration
 * @param sunsetDate an ISO 8601 date-time, or a note such as Immediate
 */
public record DoraDeprecation(
    String algorithm, String category, String recommendedMigration, String sunsetDate) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("DoraDeprecation")
        .show("algorithm", algorithm)
        .show("category", category)
        .show("recommendedMigration", recommendedMigration)
        .show("sunsetDate", sunsetDate)
        .toString();
  }
}
