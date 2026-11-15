// SPDX-License-Identifier: Apache-2.0 OR MIT

import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.HashAlgorithm;
import co.cryptoservice.sdk.HashRequest;
import co.cryptoservice.sdk.HashResult;
import co.cryptoservice.sdk.RetryOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Many requests in flight at once through one client with the async API: every call returns a
 * {@link CompletableFuture} at once, and the client's pooled HTTP client carries them all. No
 * thread is blocked while they run.
 */
public final class ConcurrentHashes {

  private ConcurrentHashes() {}

  public static void main(String[] args) {
    try (CryptoServiceClient client =
        CryptoServiceClient.builder()
            .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
            .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
            .retry(RetryOptions.of(3))
            .build()) {
      List<CompletableFuture<HashResult>> calls = new ArrayList<>();
      for (int i = 0; i < 20; i++) {
        calls.add(client.hashAsync(new HashRequest(HashAlgorithm.BLAKE3, "message " + i)));
      }
      CompletableFuture.allOf(calls.toArray(CompletableFuture[]::new)).join();
      long distinct = calls.stream().map(c -> c.join().digest()).distinct().count();
      System.out.println(calls.size() + " digests, " + distinct + " distinct");
      if (distinct != calls.size()) {
        throw new IllegalStateException("expected distinct digests");
      }
    }
  }
}
