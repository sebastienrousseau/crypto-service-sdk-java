// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Turns a 2xx response body into its typed result.
 *
 * <p>Most v2 routes wrap their result in {@code {"data": ...}}; the decoders for them read the
 * {@code data} member straight from the byte stream through a JSON pointer, without building a tree
 * of the whole body. {@code GET /}, the probes and the CBOM are not wrapped.
 *
 * @param <T> the result type
 */
@FunctionalInterface
interface Decoder<T> {

  /**
   * Decodes a body.
   *
   * @throws IOException when the body is not JSON of the expected shape
   */
  T decode(byte[] body) throws IOException;

  /** A route that answers {@code {"data": T}}. */
  static <T> Decoder<T> data(Class<T> type) {
    return fromReader(Json.MAPPER.readerFor(type).at("/data"));
  }

  /** A route that answers {@code {"data": "..."}}, a bare string. */
  static Decoder<String> dataString() {
    return data(String.class);
  }

  /** {@code GET /v2/algorithms}: {@code {"data": {category: [names]}}}. */
  static Decoder<Map<String, List<String>>> dataMap() {
    return fromReader(
        Json.MAPPER.readerFor(new TypeReference<Map<String, List<String>>>() {}).at("/data"));
  }

  /** A route whose body is the result itself. */
  static <T> Decoder<T> bare(Class<T> type) {
    return fromReader(Json.MAPPER.readerFor(type));
  }

  private static <T> Decoder<T> fromReader(ObjectReader reader) {
    return body -> {
      T value = reader.readValue(body);
      if (value == null) {
        throw new IOException("the response has no result");
      }
      return value;
    };
  }
}
