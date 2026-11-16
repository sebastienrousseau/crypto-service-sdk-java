// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.Base64;
import java.util.HexFormat;

/**
 * The two binary encodings the API uses, so callers can pass and read {@code byte[]} without
 * guessing: lowercase hex for keys, signatures, digests, MACs, shared secrets, KEM ciphertexts and
 * HPKE output; standard base64 with padding for AEAD, secretbox, sealed-box, stream,
 * password-encryption and AES-KW output. Each result record's {@code ...Bytes()} accessor applies
 * the encoding its OpenAPI schema names.
 *
 * <p>These are text encodings only; the SDK performs no cryptography.
 */
public final class Bytes {

  private static final HexFormat HEX = HexFormat.of();

  private Bytes() {}

  /**
   * Encodes bytes as lowercase hex, for request members the API reads as hex.
   *
   * @param bytes the bytes
   * @return the hex text
   */
  public static String hex(byte[] bytes) {
    return HEX.formatHex(bytes);
  }

  /**
   * Decodes hex text (either case).
   *
   * @param hex the hex text
   * @return the bytes
   * @throws IllegalArgumentException when the text is not hex of even length
   */
  public static byte[] fromHex(String hex) {
    return HEX.parseHex(hex);
  }

  /**
   * Encodes bytes as standard base64 with padding.
   *
   * @param bytes the bytes
   * @return the base64 text
   */
  public static String base64(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * Decodes standard base64.
   *
   * @param base64 the base64 text
   * @return the bytes
   * @throws IllegalArgumentException when the text is not base64
   */
  public static byte[] fromBase64(String base64) {
    return Base64.getDecoder().decode(base64);
  }

  static byte[] fromHexOrNull(String hex) {
    return hex == null ? null : fromHex(hex);
  }

  static byte[] fromBase64OrNull(String base64) {
    return base64 == null ? null : fromBase64(base64);
  }
}
