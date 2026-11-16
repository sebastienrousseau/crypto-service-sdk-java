// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An RFC 9457 problem: the body of every error the server sends ({@code
 * application/problem+json}).
 *
 * @param type {@code urn:crypto-service:problem:<slug>}, or {@code about:blank} when the server
 *     sent no problem body
 * @param title a short summary of the problem type
 * @param status the HTTP status
 * @param detail an explanation of this occurrence
 * @param instance the request path, or null
 * @param code the machine-readable code for crypto-lib, key-store and KMS errors (for example
 *     {@code AUTH_FAILED}, {@code KEY_NOT_FOUND}), or null
 * @param errors each field that failed validation ({@code validation-failed}); empty otherwise
 * @param extensions every other member (for example {@code limit} and {@code resetSeconds} on a
 *     tenant quota problem), in the order sent
 */
public record Problem(
    String type,
    String title,
    int status,
    String detail,
    String instance,
    String code,
    List<ValidationError> errors,
    Map<String, JsonNode> extensions) {

  /** The {@code type} of a problem the client made up for a body that was not one. */
  public static final String ABOUT_BLANK = "about:blank";

  private static final Set<String> KNOWN =
      Set.of("type", "title", "status", "detail", "instance", "code", "errors");

  /**
   * Copies the lists and maps so the problem is immutable.
   *
   * @throws NullPointerException when {@code errors} or {@code extensions} is null
   */
  public Problem {
    errors = List.copyOf(errors);
    extensions = Map.copyOf(extensions);
  }

  /**
   * Reads a problem from an error response body. A body that is not a JSON object with a string
   * {@code detail} (the test the TypeScript SDK applies) becomes an {@code about:blank} problem for
   * {@code status}.
   *
   * @param body the response body
   * @param status the response status
   * @return the problem; never null, and never throws
   */
  public static Problem parse(byte[] body, int status) {
    JsonNode node;
    try {
      node = Json.MAPPER.readTree(body);
    } catch (IOException | RuntimeException e) {
      return blank(status);
    }
    if (!node.path("detail").isTextual()) {
      return blank(status);
    }
    return fromObject(node, status);
  }

  /**
   * The problem the client uses when the body is not one.
   *
   * @param status the response status
   * @return an {@code about:blank} problem
   */
  public static Problem blank(int status) {
    return new Problem(
        ABOUT_BLANK,
        "HTTP error",
        status,
        "The server answered " + status + " without a problem body",
        null,
        null,
        List.of(),
        Map.of());
  }

  private static Problem fromObject(JsonNode node, int status) {
    Map<String, JsonNode> extensions = new LinkedHashMap<>();
    for (Map.Entry<String, JsonNode> e : node.properties()) {
      if (!KNOWN.contains(e.getKey())) {
        extensions.put(e.getKey(), e.getValue());
      }
    }
    return new Problem(
        textOr(node, "type", ABOUT_BLANK),
        textOr(node, "title", "HTTP error"),
        node.path("status").canConvertToInt() ? node.path("status").asInt() : status,
        node.path("detail").asText(),
        textOr(node, "instance", null),
        textOr(node, "code", null),
        validationErrors(node.path("errors")),
        extensions);
  }

  private static List<ValidationError> validationErrors(JsonNode errors) {
    List<ValidationError> out = new ArrayList<>();
    for (JsonNode e : errors) {
      out.add(new ValidationError(textOr(e, "field", ""), textOr(e, "message", "")));
    }
    return out;
  }

  private static String textOr(JsonNode node, String field, String fallback) {
    JsonNode value = node.path(field);
    return value.isTextual() ? value.asText() : fallback;
  }

  /**
   * Names the type, status, code and instance, counts the validation errors and names the
   * extension members; {@code detail} and the validation messages are left out, as they describe
   * the request.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("Problem")
        .show("type", type)
        .show("title", title)
        .show("status", status)
        .hide("detail", detail)
        .show("instance", instance)
        .show("code", code)
        .show("errors", errors.size())
        .show("extensions", extensions.keySet())
        .toString();
  }
}
