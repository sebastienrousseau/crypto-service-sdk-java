// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Synthetic values for reflective tests: every SDK record and enum, and an instance of any record
 * with every member set. The values are placeholders ({@code "abcd"} is valid hex and base64), never
 * real keys.
 */
final class Synthetic {

  static final String TEXT = "abcd";

  private Synthetic() {}

  /** Every top-level class compiled from src/main, by simple name. */
  static List<Class<?>> sdkClasses() throws IOException {
    Path dir = Path.of("target/classes/co/cryptoservice/sdk");
    List<Class<?>> out = new ArrayList<>();
    try (Stream<Path> files = Files.list(dir)) {
      for (Path file : files.sorted().toList()) {
        String name = file.getFileName().toString();
        if (name.endsWith(".class") && !name.contains("$") && !name.equals("package-info.class")) {
          out.add(load(name.substring(0, name.length() - ".class".length())));
        }
      }
    }
    return out;
  }

  private static Class<?> load(String simpleName) {
    try {
      return Class.forName("co.cryptoservice.sdk." + simpleName);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  /** Public records, the SDK's request and result types. */
  static List<Class<?>> publicRecords() throws IOException {
    return sdkClasses().stream()
        .filter(Class::isRecord)
        .filter(c -> java.lang.reflect.Modifier.isPublic(c.getModifiers()))
        .toList();
  }

  /** The records that are API bodies (not the client's own option and error types). */
  static List<Class<?>> bodyRecords() throws IOException {
    Set<Class<?>> own = Set.of(Problem.class, ResponseInfo.class, RetryOptions.class);
    return publicRecords().stream().filter(c -> !own.contains(c)).toList();
  }

  /** Fixed values for the leaf types. */
  private static final Map<Class<?>, Object> LEAVES =
      Map.ofEntries(
          Map.entry(String.class, TEXT),
          Map.entry(int.class, 1),
          Map.entry(Integer.class, 1),
          Map.entry(double.class, 1.5),
          Map.entry(Double.class, 1.5),
          Map.entry(boolean.class, true),
          Map.entry(Boolean.class, true),
          Map.entry(JsonNode.class, TextNode.valueOf(TEXT)),
          Map.entry(Duration.class, Duration.ofSeconds(1)),
          Map.entry(HttpHeaders.class, HttpHeaders.of(Map.of(), (k, v) -> true)));

  /** An instance of {@code type} with every member set to a synthetic value. */
  static Object value(Type type) {
    if (type instanceof ParameterizedType p) {
      Type element = p.getActualTypeArguments()[p.getActualTypeArguments().length - 1];
      return p.getRawType() == List.class ? List.of(value(element)) : Map.of(TEXT, value(element));
    }
    Class<?> c = (Class<?>) type;
    if (LEAVES.containsKey(c)) {
      return LEAVES.get(c);
    }
    return c.isEnum() ? c.getEnumConstants()[0] : record(c);
  }

  /** A record with every component set. */
  static Object record(Class<?> type) {
    RecordComponent[] components = type.getRecordComponents();
    Class<?>[] types = new Class<?>[components.length];
    Object[] args = new Object[components.length];
    for (int i = 0; i < components.length; i++) {
      types[i] = components[i].getType();
      args[i] = value(components[i].getGenericType());
    }
    try {
      Constructor<?> ctor = type.getDeclaredConstructor(types);
      return ctor.newInstance(args);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(e.getCause());
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }
}
