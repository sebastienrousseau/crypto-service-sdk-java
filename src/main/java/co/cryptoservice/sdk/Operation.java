// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * A typed API operation: its route and how its 2xx body decodes.
 *
 * @param <T> the result type
 */
record Operation<T>(Route route, Decoder<T> decoder) {

  /** A {@code GET} operation. */
  static <T> Operation<T> get(String path, boolean authenticated, Decoder<T> decoder) {
    return new Operation<>(Route.of("GET", path, authenticated), decoder);
  }

  /** A {@code POST} operation with a JSON body. */
  static <T> Operation<T> post(String path, boolean authenticated, Decoder<T> decoder) {
    return new Operation<>(Route.of("POST", path, authenticated), decoder);
  }

  /** Whether this operation sends a request body. */
  boolean hasBody() {
    return !"GET".equals(route.method());
  }

  /**
   * The result of a response: the decoded body for a 2xx, an {@link ApiError} otherwise.
   *
   * @throws ApiError when the status is not 2xx
   * @throws TransportError when a 2xx body does not decode
   */
  T result(HttpResponse<byte[]> response) {
    int status = response.statusCode();
    if (status / 100 != 2) {
      throw new ApiError(status, Problem.parse(response.body(), status), route);
    }
    try {
      return decoder.decode(response.body());
    } catch (IOException e) {
      throw TransportError.malformed(route);
    }
  }
}
