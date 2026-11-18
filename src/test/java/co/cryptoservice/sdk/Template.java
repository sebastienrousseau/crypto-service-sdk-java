// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The kit's template match (conformance/README.md, "expect"): exact values, element-wise arrays,
 * objects with exactly the template's members, and {@code {"$shape": schema}} matched by the subset
 * of JSON Schema 2020-12 the kit uses ({@code type}, {@code const}, {@code pattern}, {@code format}
 * date-time and uuid, {@code required}, {@code properties}, {@code additionalProperties}, {@code
 * items}).
 */
final class Template {

  private static final Pattern UUID =
      Pattern.compile(
          "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

  private Template() {}

  /** Every place {@code actual} differs from {@code template}; empty when it matches. */
  static List<String> mismatches(JsonNode template, JsonNode actual) {
    List<String> out = new ArrayList<>();
    match(template, actual, "", out);
    return out;
  }

  private static void match(JsonNode template, JsonNode actual, String at, List<String> out) {
    if (template.isObject() && template.size() == 1 && template.has("$shape")) {
      schema(template.get("$shape"), actual, at, out);
    } else if (template.isObject()) {
      matchObject(template, actual, at, out);
    } else if (template.isArray()) {
      matchArray(template, actual, at, out);
    } else if (!sameValue(template, actual)) {
      out.add(at + ": expected " + template + ", got " + brief(actual));
    }
  }

  private static boolean sameValue(JsonNode a, JsonNode b) {
    if (a.isNumber() && b.isNumber()) {
      return a.decimalValue().compareTo(b.decimalValue()) == 0;
    }
    return a.equals(b);
  }

  private static void matchObject(JsonNode template, JsonNode actual, String at, List<String> out) {
    if (!actual.isObject()) {
      out.add(at + ": expected an object, got " + brief(actual));
      return;
    }
    for (Map.Entry<String, JsonNode> e : actual.properties()) {
      if (!template.has(e.getKey())) {
        out.add(at + "/" + e.getKey() + ": unexpected member");
      }
    }
    for (Map.Entry<String, JsonNode> e : template.properties()) {
      JsonNode value = actual.get(e.getKey());
      if (value == null) {
        out.add(at + "/" + e.getKey() + ": missing");
      } else {
        match(e.getValue(), value, at + "/" + e.getKey(), out);
      }
    }
  }

  private static void matchArray(JsonNode template, JsonNode actual, String at, List<String> out) {
    if (!actual.isArray() || actual.size() != template.size()) {
      out.add(at + ": expected an array of " + template.size() + ", got " + brief(actual));
      return;
    }
    for (int i = 0; i < template.size(); i++) {
      match(template.get(i), actual.get(i), at + "/" + i, out);
    }
  }

  /** The {@code $shape} subset of JSON Schema. */
  static void schema(JsonNode schema, JsonNode value, String at, List<String> out) {
    if (schema.has("const") && !sameValue(schema.get("const"), value)) {
      out.add(at + ": expected const " + schema.get("const"));
    }
    if (schema.has("type") && !hasType(schema.get("type").asText(), value)) {
      out.add(at + ": expected type " + schema.get("type").asText() + ", got " + brief(value));
      return;
    }
    if (value.isTextual()) {
      stringRules(schema, value.asText(), at, out);
    }
    if (value.isObject()) {
      objectRules(schema, value, at, out);
    }
    if (value.isArray() && schema.has("items")) {
      for (int i = 0; i < value.size(); i++) {
        schema(schema.get("items"), value.get(i), at + "/" + i, out);
      }
    }
  }

  private static boolean hasType(String type, JsonNode value) {
    return switch (type) {
      case "string" -> value.isTextual();
      case "integer" -> value.isIntegralNumber();
      case "number" -> value.isNumber();
      case "boolean" -> value.isBoolean();
      case "object" -> value.isObject();
      case "array" -> value.isArray();
      case "null" -> value.isNull();
      default -> false;
    };
  }

  private static void stringRules(JsonNode schema, String text, String at, List<String> out) {
    if (schema.has("pattern")
        && !Pattern.compile(schema.get("pattern").asText()).matcher(text).find()) {
      out.add(at + ": does not match " + schema.get("pattern").asText());
    }
    String format = schema.path("format").asText("");
    if (format.equals("uuid") && !UUID.matcher(text).matches()) {
      out.add(at + ": not a uuid");
    }
    if (format.equals("date-time") && !isDateTime(text)) {
      out.add(at + ": not a date-time");
    }
  }

  private static boolean isDateTime(String text) {
    try {
      OffsetDateTime.parse(text);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  private static void objectRules(JsonNode schema, JsonNode value, String at, List<String> out) {
    for (JsonNode required : schema.path("required")) {
      if (!value.has(required.asText())) {
        out.add(at + "/" + required.asText() + ": required");
      }
    }
    JsonNode properties = schema.path("properties");
    JsonNode additional = schema.path("additionalProperties");
    for (Map.Entry<String, JsonNode> e : value.properties()) {
      String where = at + "/" + e.getKey();
      if (properties.has(e.getKey())) {
        schema(properties.get(e.getKey()), e.getValue(), where, out);
      } else if (additional.isObject()) {
        schema(additional, e.getValue(), where, out);
      } else if (additional.isBoolean() && !additional.asBoolean()) {
        out.add(where + ": not allowed");
      }
    }
  }

  /** A short description of a value for a mismatch message. */
  private static String brief(JsonNode value) {
    String text = value.toString();
    return text.length() > 80 ? text.substring(0, 80) + "..." : text;
  }
}
