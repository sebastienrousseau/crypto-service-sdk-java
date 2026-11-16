// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Runs the vendored conformance kit ({@code conformance/}, see its README): file by file, vector by
 * vector, with {@code ${name}} captures kept for the rest of the file.
 *
 * <p>A vector goes through the SDK's public typed method for its route when one exists and its
 * body binds to the method's request type without loss; otherwise (unknown route, malformed body,
 * a member the request type does not model, the v1 routes, {@code /metrics}) it goes through
 * {@link CryptoServiceClient#request}. A typed answer is turned back into JSON (the result record,
 * wrapped in {@code data} where the route wraps it, or the problem of an {@link ApiError}) and
 * matched against the template, so the record must carry every member the server sends.
 */
final class ConformanceKit {

  static final Path DIR = Path.of("conformance");
  private static final Pattern VARIABLE = Pattern.compile("\\$\\{([A-Za-z0-9_]+)\\}");

  /** The outcome of one vector: status, media type, headers, body, and which path ran it. */
  record Observed(int status, String mediaType, ResponseInfo info, JsonNode body, boolean typed) {}

  private final Map<String, List<Method>> typed;

  /** Vectors run through a typed method, and through the raw request. */
  final java.util.concurrent.atomic.AtomicInteger typedRuns = new java.util.concurrent.atomic.AtomicInteger();

  final java.util.concurrent.atomic.AtomicInteger rawRuns = new java.util.concurrent.atomic.AtomicInteger();

  ConformanceKit(Map<String, List<Method>> typed) {
    this.typed = typed;
  }

  /** The kit's vector files, sorted. */
  static List<Path> files() throws IOException {
    try (Stream<Path> files = Files.list(DIR)) {
      return files.filter(p -> p.toString().endsWith(".json")).sorted().toList();
    }
  }

  static JsonNode read(Path file) throws IOException {
    return Json.MAPPER.readTree(Files.readAllBytes(file));
  }

  /** {@code node} with every {@code ${name}} in its strings replaced. */
  static JsonNode substitute(JsonNode node, Map<String, String> captured) {
    if (node.isTextual()) {
      Matcher m = VARIABLE.matcher(node.asText());
      StringBuilder out = new StringBuilder();
      while (m.find()) {
        String value = captured.get(m.group(1));
        if (value == null) {
          throw new IllegalStateException("no capture named " + m.group(1));
        }
        m.appendReplacement(out, Matcher.quoteReplacement(value));
      }
      m.appendTail(out);
      return TextNode.valueOf(out.toString());
    }
    JsonNode copy = node.deepCopy();
    if (copy.isObject()) {
      node.properties().forEach(e -> ((ObjectNode) copy).set(e.getKey(), substitute(e.getValue(), captured)));
    } else if (copy.isArray()) {
      for (int i = 0; i < node.size(); i++) {
        ((com.fasterxml.jackson.databind.node.ArrayNode) copy).set(i, substitute(node.get(i), captured));
      }
    }
    return copy;
  }

  /** Runs one vector; returns every mismatch (empty when it passes) and records its captures. */
  List<String> run(JsonNode vector, KitAuth auth, Map<String, String> captured) throws IOException {
    JsonNode request = substitute(vector.get("request"), captured);
    JsonNode expect = substitute(vector.get("expect"), captured);
    CryptoServiceClient client = auth.client(vector.get("auth"));
    Observed seen = typedCall(client, request, auth).orElseGet(() -> rawCall(client, request, auth));
    (seen.typed() ? typedRuns : rawRuns).incrementAndGet();
    List<String> problems = new ArrayList<>();
    if (seen.status() != expect.get("status").asInt()) {
      problems.add("status " + seen.status() + ", expected " + expect.get("status").asInt());
    }
    if (!expect.get("mediaType").asText().equals(seen.mediaType())) {
      problems.add("media type " + seen.mediaType() + ", expected " + expect.get("mediaType").asText());
    }
    for (Map.Entry<String, JsonNode> h : expect.path("headers").properties()) {
      Optional<String> value = seen.info().headers().firstValue(h.getKey());
      if (!value.equals(Optional.of(h.getValue().asText()))) {
        problems.add("header " + h.getKey() + " is " + value.orElse("absent"));
      }
    }
    JsonNode template = expect.has("body") ? expect.get("body") : expect.get("text");
    if (template != null) {
      problems.addAll(Template.mismatches(template, seen.body()));
    }
    for (Map.Entry<String, JsonNode> c : vector.path("capture").properties()) {
      captured.put(c.getKey(), seen.body().at(JsonPointer.compile(c.getValue().asText())).asText());
    }
    return problems;
  }

  /** The vector through a typed method, or empty when none applies. */
  private Optional<Observed> typedCall(CryptoServiceClient client, JsonNode request, KitAuth auth) {
    String route = request.get("method").asText() + " " + request.get("path").asText();
    if (request.has("rawBody") || request.has("headers") || !typed.containsKey(route)) {
      return Optional.empty();
    }
    for (Method m : typed.get(route)) {
      Optional<Object[]> args = bind(m, request.get("body"));
      if (args.isPresent()) {
        return Optional.of(invoke(m, client, args.get(), auth));
      }
    }
    return Optional.empty();
  }

  /** The arguments for {@code m} from {@code body}, if they re-serialise to exactly that body. */
  private static Optional<Object[]> bind(Method m, JsonNode body) {
    if (m.getParameterCount() == 0) {
      boolean empty = body == null || (body.isObject() && body.isEmpty());
      return empty ? Optional.of(new Object[0]) : Optional.empty();
    }
    if (body == null) {
      return Optional.empty();
    }
    try {
      Object arg = Json.convert(body, m.getParameterTypes()[0]);
      boolean lossless = Json.MAPPER.valueToTree(arg).equals(body);
      return lossless ? Optional.of(new Object[] {arg}) : Optional.empty();
    } catch (IOException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private static Observed invoke(Method m, CryptoServiceClient client, Object[] args, KitAuth auth) {
    try {
      Object result = m.invoke(client, args);
      JsonNode tree = Json.MAPPER.valueToTree(result);
      boolean bare = TypedRoutes.BARE.contains(m.getReturnType());
      JsonNode body = bare ? tree : Json.MAPPER.createObjectNode().set("data", tree);
      ResponseInfo info = auth.lastResponse();
      return new Observed(info.status(), info.mediaType().orElse(""), info, body, true);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof ApiError error) {
        ResponseInfo info = auth.lastResponse();
        return new Observed(error.status(), info.mediaType().orElse(""), info, problemJson(error.problem()), true);
      }
      throw new IllegalStateException(e.getCause());
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  /** A problem as the JSON the server sent. */
  static JsonNode problemJson(Problem p) {
    ObjectNode out = Json.MAPPER.createObjectNode();
    out.put("type", p.type()).put("title", p.title()).put("status", p.status()).put("detail", p.detail());
    if (p.instance() != null) {
      out.put("instance", p.instance());
    }
    if (p.code() != null) {
      out.put("code", p.code());
    }
    if (!p.errors().isEmpty()) {
      out.set("errors", Json.MAPPER.valueToTree(p.errors()));
    }
    p.extensions().forEach(out::set);
    return out;
  }

  private static Observed rawCall(CryptoServiceClient client, JsonNode request, KitAuth auth) {
    byte[] body = null;
    if (request.has("rawBody")) {
      body = request.get("rawBody").asText().getBytes(StandardCharsets.UTF_8);
    } else if (request.has("body")) {
      body = Json.encode(request.get("body"));
    }
    String type = request.path("headers").path("content-type").asText(null);
    RawResponse r = client.request(request.get("method").asText(), request.get("path").asText(), body, type);
    String mediaType = r.mediaType().orElse("");
    JsonNode parsed = TextNode.valueOf(r.bodyText());
    if (mediaType.endsWith("json")) {
      try {
        parsed = Json.MAPPER.readTree(r.body());
      } catch (IOException e) {
        parsed = TextNode.valueOf(r.bodyText());
      }
    }
    return new Observed(r.status(), mediaType, auth.lastResponse(), parsed, false);
  }
}
