// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The one place the SDK configures Jackson.
 *
 * <p>Requests leave out null members, so an optional member the caller did not set is not sent.
 * Responses ignore members this version does not know, so a newer server does not break an older
 * client.
 */
final class Json {

  /** The shared mapper; Jackson mappers are thread-safe once configured. */
  static final ObjectMapper MAPPER =
      JsonMapper.builder()
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .defaultPropertyInclusion(
              JsonInclude.Value.construct(
                  JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
          .build();

  /** {@code {}}, the body of a POST whose options are all left to the server. */
  static final ObjectNode EMPTY_OBJECT = MAPPER.createObjectNode();

  /** The media type of every request body the typed methods send. */
  static final String MEDIA_TYPE = "application/json";

  private Json() {}

  /**
   * Serialises a request body to UTF-8 JSON bytes.
   *
   * @throws IllegalArgumentException when the value cannot be written as JSON
   */
  static byte[] encode(Object value) {
    try {
      return MAPPER.writeValueAsBytes(value);
    } catch (JsonProcessingException e) {
      String type = value.getClass().getSimpleName();
      throw new IllegalArgumentException(
          "request body of type " + type + " cannot be written as JSON");
    }
  }

  /** Converts a JSON tree to a typed value; used by tests and the conformance kit. */
  static <T> T convert(JsonNode node, Class<T> type) throws JsonProcessingException {
    return MAPPER.treeToValue(node, type);
  }
}
