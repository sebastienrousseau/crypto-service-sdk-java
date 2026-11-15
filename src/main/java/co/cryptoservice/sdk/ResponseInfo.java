// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.net.http.HttpHeaders;
import java.util.Optional;

/**
 * What a {@link ResponseListener} learns about one HTTP response: never a body.
 *
 * @param method the HTTP method
 * @param path the path below the base URL
 * @param status the HTTP status
 * @param headers the response headers (for example {@code X-Tenant-RateLimit-Remaining})
 * @param attempt the attempt that got this response, from 1
 */
public record ResponseInfo(
    String method, String path, int status, HttpHeaders headers, int attempt) {

  /**
   * The {@code Content-Type} without parameters, lower-cased.
   *
   * @return for example {@code application/problem+json}, or empty when there is none
   */
  public Optional<String> mediaType() {
    return headers.firstValue("content-type").map(ResponseInfo::bareMediaType);
  }

  static String bareMediaType(String contentType) {
    int semicolon = contentType.indexOf(';');
    String bare = semicolon < 0 ? contentType : contentType.substring(0, semicolon);
    return bare.strip().toLowerCase(java.util.Locale.ROOT);
  }
}
