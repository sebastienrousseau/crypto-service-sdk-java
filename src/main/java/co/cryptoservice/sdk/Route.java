// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.regex.Pattern;

/**
 * One HTTP route: its method and path, whether the client sends credentials, and whether repeating
 * it is safe after the server may already have acted on it.
 *
 * <p>The retry-safety table is the TypeScript SDK's ({@code request.ts}): a {@code GET}, and every
 * route that neither creates nor deletes server state, is idempotent. Key generation ({@code
 * /v2/keys/generate} and every {@code /keygen}), {@code /v2/keys/delete}, {@code
 * /v2/kms/create-key} and {@code /v2/opaque/register/*} are not: after a lost connection or a 504
 * the server may already have done the work, so they are retried only after 429 or 503.
 */
record Route(String method, String path, boolean authenticated, boolean idempotent) {

  private static final Pattern STATEFUL =
      Pattern.compile("/(keygen|keys/generate|keys/delete|kms/create-key|opaque/register/)");

  /** A route whose idempotency follows the retry-safety table. */
  static Route of(String method, String path, boolean authenticated) {
    return new Route(method, path, authenticated, isIdempotent(method, path));
  }

  /** Whether repeating {@code method path} cannot create or delete anything twice. */
  static boolean isIdempotent(String method, String path) {
    return "GET".equals(method) || !STATEFUL.matcher(path).find();
  }
}
