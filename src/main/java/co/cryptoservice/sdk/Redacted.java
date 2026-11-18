// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Locale;

/**
 * Builds a {@code toString} that names every member but shows only the values that cannot be
 * secret. A hidden value prints as {@code <redacted>}, or {@code null} when it is absent; a shown
 * value is {@linkplain #printable printable}.
 */
final class Redacted {

  private final StringBuilder out;
  private boolean first = true;

  Redacted(String type) {
    this.out = new StringBuilder(type).append('[');
  }

  /** Appends a member and its value. */
  Redacted show(String name, Object value) {
    return append(name, printable(String.valueOf(value)));
  }

  /** Appends a member with its value redacted. */
  Redacted hide(String name, Object value) {
    return append(name, value == null ? "null" : "<redacted>");
  }

  private Redacted append(String name, String text) {
    if (!first) {
      out.append(", ");
    }
    first = false;
    out.append(name).append('=').append(text);
    return this;
  }

  /**
   * {@code text} with every control character and Unicode line or paragraph separator replaced by
   * its six-character Java escape (a backslash, {@code u} and four hex digits), so a value the
   * server chose cannot start a new line in a log.
   */
  static String printable(String text) {
    StringBuilder escaped = new StringBuilder(text.length());
    for (char c : text.toCharArray()) {
      if (breaksLines(c)) {
        escaped.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
      } else {
        escaped.append(c);
      }
    }
    return escaped.toString();
  }

  private static boolean breaksLines(char c) {
    return Character.isISOControl(c) || c == '\u2028' || c == '\u2029';
  }

  @Override
  public String toString() {
    return out.toString() + ']';
  }
}
