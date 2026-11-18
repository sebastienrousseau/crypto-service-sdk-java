// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * The answer to {@link CryptoServiceClient#request}, whatever its status: the low-level escape
 * hatch for routes the typed methods do not cover.
 */
public final class RawResponse {

  private final int status;
  private final HttpHeaders headers;
  private final byte[] body;

  RawResponse(int status, HttpHeaders headers, byte[] body) {
    this.status = status;
    this.headers = headers;
    this.body = body;
  }

  /**
   * The HTTP status.
   *
   * @return the status, 2xx or not
   */
  public int status() {
    return status;
  }

  /**
   * The response headers.
   *
   * @return the headers
   */
  public HttpHeaders headers() {
    return headers;
  }

  /**
   * The {@code Content-Type} without parameters, lower-cased.
   *
   * @return for example {@code application/json}, or empty when there is none
   */
  public Optional<String> mediaType() {
    return headers.firstValue("content-type").map(ResponseInfo::bareMediaType);
  }

  /**
   * The body.
   *
   * @return a copy of the body bytes
   */
  public byte[] body() {
    return body.clone();
  }

  /**
   * The body decoded as UTF-8.
   *
   * @return the body text
   */
  public String bodyText() {
    return new String(body, StandardCharsets.UTF_8);
  }

  /**
   * The status and media type only; the body may hold secrets.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("RawResponse")
        .show("status", status)
        .show("mediaType", mediaType().orElse(null))
        .hide("body", body)
        .toString();
  }
}
