// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * The client's own cost, no network. The scenario names are the same in every Crypto Service SDK:
 *
 * <ul>
 *   <li>{@code encode_request}: a {@code POST /v2/hash} request serialised to JSON and built into
 *       the {@link HttpRequest} the client sends (headers, credentials, timeout);
 *   <li>{@code decode_response}: a captured {@code /v2/hash} 200 body decoded into {@link
 *       HashResult};
 *   <li>{@code decode_problem}: a captured {@code validation-failed} body parsed into a {@link
 *       Problem}.
 * </ul>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ClientSideBenchmark {

  private static final Route HASH = Route.of("POST", "/v2/hash", true);

  private final CryptoServiceClient client =
      CryptoServiceClient.builder().baseUrl("http://127.0.0.1:3101").apiKey("bench-key").build();
  private final HashRequest request = new HashRequest(HashAlgorithm.SHA256, "abc");
  private final Decoder<HashResult> decoder = Decoder.data(HashResult.class);
  private final byte[] hashBody = resource("hash.json");
  private final byte[] problemBody = resource("problem.json");

  private static byte[] resource(String name) {
    try (InputStream in = ClientSideBenchmark.class.getResourceAsStream("bench/" + name)) {
      return in.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Benchmark
  public HttpRequest encode_request() {
    return client.transport.request(HASH, Json.encode(request), Json.MEDIA_TYPE);
  }

  @Benchmark
  public HashResult decode_response() throws IOException {
    return decoder.decode(hashBody);
  }

  @Benchmark
  public Problem decode_problem() {
    return Problem.parse(problemBody, 400);
  }
}
