// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * Builds a {@code toString} that names every member but shows only the values that cannot be
 * secret. A hidden value prints as {@code <redacted>}, or {@code null} when it is absent.
 */
final class Redacted {

  private final StringBuilder out;
  private boolean first = true;

  Redacted(String type) {
    this.out = new StringBuilder(type).append('[');
  }

  /** Appends a member and its value. */
  Redacted show(String name, Object value) {
    return append(name, String.valueOf(value));
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

  @Override
  public String toString() {
    return out.toString() + ']';
  }
}
