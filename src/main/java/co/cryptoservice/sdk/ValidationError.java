// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * One field that failed validation, in a {@code validation-failed} problem.
 *
 * @param field the field, as a JSON pointer (for example {@code /data}) or a body member name
 * @param message why it failed
 */
public record ValidationError(String field, String message) {

  /**
   * Names the field and the message, both as the server sent them but with any control character
   * escaped, so the text cannot start a new line in a log.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("ValidationError").show("field", field).show("message", message).toString();
  }
}
