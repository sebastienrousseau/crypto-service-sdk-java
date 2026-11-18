// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * What {@code GET /v2/compliance/cbom} answers: cryptographic Bill of Materials (CBOM) in CycloneDX
 * 1.6 format.
 *
 * @param bomFormat always CycloneDX
 * @param components the components
 * @param metadata the key metadata, or null
 * @param serialNumber A fresh urn:uuid for every document
 * @param specVersion cycloneDX specification version
 * @param version BOM version
 */
public record CbomDocument(
    String bomFormat,
    List<CbomComponent> components,
    JsonNode metadata,
    String serialNumber,
    String specVersion,
    int version) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("CbomDocument")
        .show("bomFormat", bomFormat)
        .hide("components", components)
        .hide("metadata", metadata)
        .show("serialNumber", serialNumber)
        .show("specVersion", specVersion)
        .show("version", version)
        .toString();
  }
}
