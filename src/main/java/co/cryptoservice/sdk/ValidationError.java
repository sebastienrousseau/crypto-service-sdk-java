// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * One field that failed validation, in a {@code validation-failed} problem.
 *
 * @param field the field, as a JSON pointer (for example {@code /data}) or a body member name
 * @param message why it failed
 */
public record ValidationError(String field, String message) {}
