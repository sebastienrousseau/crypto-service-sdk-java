// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.net.http.HttpTimeoutException;

/**
 * No usable answer arrived: the connection failed, an attempt or the overall deadline timed out,
 * the calling thread was interrupted, or a 2xx body could not be decoded.
 *
 * <p>{@link #timedOut() timedOut()} tells a timeout from the other causes. The cause, when there is
 * one, is the I/O exception the HTTP client raised; a decoding failure carries none, because the
 * parser's message could quote the body.
 */
public final class TransportError extends CryptoServiceException {

  private static final long serialVersionUID = 1L;

  /** Whether a timeout (an attempt's or the deadline) ended the call. */
  private final boolean timedOut;

  private TransportError(String message, Throwable cause, boolean timedOut, Route route) {
    super(message + " (" + route.method() + " " + route.path() + ")", cause, route);
    this.timedOut = timedOut;
  }

  /** No response arrived for this attempt. */
  static TransportError of(Route route, Throwable cause) {
    boolean timedOut = cause instanceof HttpTimeoutException;
    String message = timedOut ? "request timed out" : "network error";
    return new TransportError(message, cause, timedOut, route);
  }

  /** The overall deadline passed. */
  static TransportError deadline(Route route) {
    return new TransportError("deadline exceeded", null, true, route);
  }

  /** The thread waiting on a blocking call was interrupted. */
  static TransportError interrupted(Route route, InterruptedException cause) {
    return new TransportError("interrupted", cause, false, route);
  }

  /** A 2xx body did not decode into the result type. */
  static TransportError malformed(Route route) {
    return new TransportError("malformed response body", null, false, route);
  }

  /**
   * Whether a timeout ended the call: the per-attempt timeout or the overall deadline.
   *
   * @return true for a timeout
   */
  public boolean timedOut() {
    return timedOut;
  }
}
