// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.HexFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * W3C Trace Context {@code traceparent} values: a fixed one, or a fresh one per request.
 *
 * <p>Generated values are version {@code 00}, a random non-zero 16-byte trace id, a random non-zero
 * 8-byte parent id and the sampled flag, as the TypeScript SDK generates them. Trace ids need to be
 * unique, not secret, so a fast non-cryptographic generator is used.
 */
final class Traceparent {

  private static final Pattern FORMAT =
      Pattern.compile("[0-9a-f]{2}-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}");
  private static final HexFormat HEX = HexFormat.of();

  private Traceparent() {}

  /**
   * Checks a caller-supplied value.
   *
   * @throws IllegalArgumentException when it is not {@code version-traceid-parentid-flags}
   */
  static String validate(String value) {
    String text = value.strip();
    if (!FORMAT.matcher(text).matches()) {
      throw new IllegalArgumentException("traceparent must be a W3C traceparent header value");
    }
    return text;
  }

  /** A supplier of a new value per request. */
  static Supplier<String> generator() {
    return () -> generate(ThreadLocalRandom.current());
  }

  /** One new value from {@code random}. */
  static String generate(Random random) {
    byte[] bytes = new byte[24];
    random.nextBytes(bytes);
    bytes[0] = (byte) (Byte.toUnsignedInt(bytes[0]) % 255 + 1);
    bytes[16] = (byte) (Byte.toUnsignedInt(bytes[16]) % 255 + 1);
    String hex = HEX.formatHex(bytes);
    return "00-" + hex.substring(0, 32) + "-" + hex.substring(32, 48) + "-01";
  }
}
