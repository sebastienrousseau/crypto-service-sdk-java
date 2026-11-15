// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * Observes every HTTP response the client receives, retried attempts included: for metrics, quota
 * tracking ({@code X-Tenant-RateLimit-*}) or tests. It sees status and headers, never a body.
 *
 * <p>It runs on the HTTP client's thread before the call completes, so it should be quick and must
 * be thread-safe. An exception it throws is ignored.
 */
@FunctionalInterface
public interface ResponseListener {

  /**
   * Called once per response.
   *
   * @param info the response's method, path, status, headers and attempt number
   */
  void onResponse(ResponseInfo info);
}
