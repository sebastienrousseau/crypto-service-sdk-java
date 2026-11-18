// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

/**
 * Every vector of the vendored kit against the live server in {@code CRYPTO_SERVICE_URL}. Skipped
 * by a plain {@code mvn verify} without a server; {@code make conformance} fails instead.
 */
class ConformanceTest {

  private static final List<KitAuth> OPEN = new ArrayList<>();
  private static ConformanceKit kit;

  @TestFactory
  List<DynamicNode> everyVectorHoldsForTheServer() throws IOException {
    KitAuth.fromEnvironment("probe").close();
    kit = new ConformanceKit(TypedRoutes.learn());
    List<DynamicNode> files = new ArrayList<>();
    for (Path file : ConformanceKit.files()) {
      JsonNode doc = ConformanceKit.read(file);
      KitAuth auth = KitAuth.fromEnvironment(doc.get("group").asText());
      OPEN.add(auth);
      Map<String, String> captured = new HashMap<>();
      List<DynamicNode> vectors = new ArrayList<>();
      for (JsonNode vector : doc.get("vectors")) {
        vectors.add(
            dynamicTest(
                vector.get("id").asText(),
                () -> {
                  List<String> problems = kit.run(vector, auth, captured);
                  assertEquals(List.of(), problems, vector.get("description").asText());
                }));
      }
      files.add(dynamicContainer(file.getFileName().toString(), vectors));
    }
    return files;
  }

  @AfterAll
  static void closeClients() {
    OPEN.forEach(KitAuth::close);
    if (kit != null) {
      System.out.println(
          "conformance: "
              + kit.typedRuns.get()
              + " vectors through typed methods, "
              + kit.rawRuns.get()
              + " through the raw request");
    }
  }
}
