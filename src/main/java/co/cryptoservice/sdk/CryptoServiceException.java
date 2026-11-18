// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

/**
 * The base of every error the client raises for a call: {@link ApiError} when the server answered
 * with an error status, {@link TransportError} when no usable answer arrived.
 *
 * <p>Messages name the method, path and status only. They never contain credentials, keys,
 * plaintexts or request and response bodies, so they are safe to log.
 */
public abstract sealed class CryptoServiceException extends RuntimeException
    permits ApiError, TransportError {

  private static final long serialVersionUID = 1L;

  /** The HTTP method of the failed call. */
  private final String method;

  /** The path of the failed call. */
  private final String path;

  CryptoServiceException(String message, Throwable cause, Route route) {
    super(message, cause);
    this.method = route.method();
    this.path = route.path();
  }

  /**
   * The HTTP method of the failed call.
   *
   * @return for example {@code POST}
   */
  public String method() {
    return method;
  }

  /**
   * The path of the failed call, below the base URL.
   *
   * @return for example {@code /v2/sign}
   */
  public String path() {
    return path;
  }
}
