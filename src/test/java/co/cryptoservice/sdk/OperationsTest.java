// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

/**
 * Every typed operation, async and blocking, against a loopback server that answers each route with
 * the smallest body its decoder accepts: the method reaches the documented route and decodes.
 */
class OperationsTest {

  @Test
  void everyOperationReachesItsRouteAndDecodes() throws Exception {
    Set<String> routes = new TreeSet<>();
    int operations = 0;
    try (TestServer server = new TestServer(TypedRoutes::answer);
        CryptoServiceClient client = server.client().build()) {
      for (Method async : asyncMethods()) {
        Object[] args = arguments(async);
        Object result = ((CompletableFuture<?>) async.invoke(client, args)).join();
        assertNotNull(result, async.getName());
        String blocking = async.getName().substring(0, async.getName().length() - "Async".length());
        Method sync = CryptoServiceClient.class.getMethod(blocking, async.getParameterTypes());
        assertNotNull(sync.invoke(client, args), blocking);
        operations++;
      }
      for (TestServer.Seen seen : server.seen()) {
        routes.add(seen.method() + " " + seen.path());
      }
    }
    assertEquals(66, operations, "typed operations, the three no-argument overloads included");
    assertEquals(63, routes.size(), String.join("\n", routes));
    assertTrue(routes.contains("GET /v2/compliance/dora"));
    assertTrue(routes.contains("POST /v2/stream/iso20022"));
  }

  @Test
  void noArgumentOverloadsSendAnEmptyObject() throws IOException {
    try (TestServer server = new TestServer(TypedRoutes::answer);
        CryptoServiceClient client = server.client().build()) {
      client.generateKeyPair();
      client.hpkeGenerateKeyPair();
      client.kmsCreateKey();
      client.mlKemGenerateKeyPair();
      client.pqGenerateKeyPair();
      for (TestServer.Seen seen : server.seen()) {
        assertEquals("{}", seen.text(), seen.path());
      }
    }
  }

  private static List<Method> asyncMethods() {
    List<Method> out = new ArrayList<>();
    for (Method m : CryptoServiceClient.class.getMethods()) {
      boolean typed = m.getName().endsWith("Async") && !m.getName().equals("requestAsync");
      if (typed && Modifier.isPublic(m.getModifiers())) {
        out.add(m);
      }
    }
    return out;
  }

  private static Object[] arguments(Method m) {
    Object[] args = new Object[m.getParameterCount()];
    for (int i = 0; i < args.length; i++) {
      args[i] = Synthetic.value(m.getGenericParameterTypes()[i]);
    }
    return args;
  }
}
