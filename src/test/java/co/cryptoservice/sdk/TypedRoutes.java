// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import co.cryptoservice.sdk.TestServer.Answer;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Which public method of {@link CryptoServiceClient} serves each route. The map is learned, not
 * written by hand: every blocking typed method is called once against a loopback server that
 * records the route it reached, so it cannot drift from the client.
 */
final class TypedRoutes {

  /** Result types the API does not wrap in {@code data}. */
  static final Set<Class<?>> BARE =
      Set.of(
          HealthResult.class,
          LiveResult.class,
          ReadyResult.class,
          ServiceInfo.class,
          CbomDocument.class);

  private static final Set<String> STRING_DATA =
      Set.of(
          "/v2/secretbox/open",
          "/v2/sealedbox/open",
          "/v2/sealedbox/open-pq",
          "/v2/password/decrypt",
          "/v2/keys/unwrap");

  private TypedRoutes() {}

  /** The typed methods for {@code "METHOD /path"}, one-argument forms first. */
  static Map<String, List<Method>> learn() throws IOException {
    Map<String, List<Method>> routes = new HashMap<>();
    try (TestServer server = new TestServer(TypedRoutes::answer);
        CryptoServiceClient client = server.client().build()) {
      for (Method m : CryptoServiceClient.class.getMethods()) {
        if (!m.getName().endsWith("Async") || m.getName().equals("requestAsync")) {
          continue;
        }
        Object[] args = new Object[m.getParameterCount()];
        for (int i = 0; i < args.length; i++) {
          args[i] = Synthetic.value(m.getGenericParameterTypes()[i]);
        }
        int before = server.seen().size();
        ((CompletableFuture<?>) invoke(m, client, args)).join();
        TestServer.Seen seen = server.seen().get(before);
        Method blocking =
            CryptoServiceClient.class.getMethod(
                m.getName().substring(0, m.getName().length() - 5), m.getParameterTypes());
        List<Method> list =
            routes.computeIfAbsent(seen.method() + " " + seen.path(), k -> new ArrayList<>());
        list.add(blocking);
        list.sort((a, b) -> b.getParameterCount() - a.getParameterCount());
      }
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
    return routes;
  }

  private static Object invoke(Method m, Object target, Object[] args) {
    try {
      return m.invoke(target, args);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  /** The smallest body each route's decoder accepts. */
  static Answer answer(TestServer.Seen seen, int index) {
    String path = seen.path();
    if (Set.of("/", "/health", "/live", "/ready", "/v2/compliance/cbom").contains(path)) {
      return Answer.json(200, "{}");
    }
    if (STRING_DATA.contains(path)) {
      return Answer.json(200, "{\"data\":\"x\"}");
    }
    return Answer.json(200, "{\"data\":{}}");
  }
}
