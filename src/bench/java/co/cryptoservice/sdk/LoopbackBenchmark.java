// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * {@code round_trip_hash}: one blocking {@code client.hash(...)} against a crypto-server on
 * loopback at {@code CRYPTO_SERVICE_URL}, so the server's work, the HTTP exchange and the client
 * together. {@code make bench} skips it with a message when the variable is unset.
 *
 * <p>crypto-server meters every principal at 60 requests a minute, so this scenario takes single
 * timed calls (5 warm-up, 50 measured) rather than a saturating loop, and runs as a fresh principal
 * when {@code CRYPTO_SERVICE_JWT_SECRET} is set (with the API key otherwise).
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 50)
public class LoopbackBenchmark {

  private CryptoServiceClient client;
  private final HashRequest request = new HashRequest(HashAlgorithm.SHA256, "abc");

  @Setup
  public void connect() {
    String url = System.getenv("CRYPTO_SERVICE_URL");
    if (url == null || url.isBlank()) {
      throw new IllegalStateException("set CRYPTO_SERVICE_URL to a crypto-server on loopback");
    }
    CryptoServiceClient.Builder builder = CryptoServiceClient.builder().baseUrl(url);
    String secret = System.getenv("CRYPTO_SERVICE_JWT_SECRET");
    if (secret != null && !secret.isBlank()) {
      builder.token(TestTokens.mint(secret, "bench-" + System.nanoTime(), List.of("crypto:hash")));
    } else {
      builder.apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"));
    }
    client = builder.build();
  }

  @TearDown
  public void close() {
    client.close();
  }

  @Benchmark
  public HashResult round_trip_hash() {
    return client.hash(request);
  }
}
