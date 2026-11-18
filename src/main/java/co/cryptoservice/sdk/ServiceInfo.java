// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * What {@code GET /} answers: service metadata.
 *
 * @param description the description
 * @param details the details
 * @param id identifier of this server instance, new at each start
 * @param license the license
 * @param title the title
 * @param url the url
 * @param version the library version as a JSON string literal, quotes included: the characters
 *     "1.2.3" with their quotes; parse it as JSON to get the version
 */
public record ServiceInfo(
    String description,
    String details,
    String id,
    String license,
    String title,
    String url,
    String version) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("ServiceInfo")
        .hide("description", description)
        .hide("details", details)
        .show("id", id)
        .show("license", license)
        .show("title", title)
        .show("url", url)
        .show("version", version)
        .toString();
  }
}
