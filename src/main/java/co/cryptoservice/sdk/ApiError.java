// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * The server answered with a status outside 2xx.
 *
 * <p>{@link #problem()} is the RFC 9457 problem the server sent. A body that is not a problem (an
 * HTML page from a proxy, an empty body, a redirect the client refused to follow) becomes an {@code
 * about:blank} problem for the status, so the problem always has the same shape.
 *
 * <pre>{@code
 * try {
 *   client.sign(new SignRequest("k_AAAAAAAAAAAAAAAAAAAAAA", "x"));
 * } catch (ApiError e) {
 *   System.err.println(e.status() + " " + e.problem().type() + " " + e.problem().code());
 * }
 * }</pre>
 *
 * <p>The name follows the error model all the Crypto Service SDKs share; it is an unchecked
 * exception, not a {@link java.lang.Error}.
 */
public final class ApiError extends CryptoServiceException {

  private static final long serialVersionUID = 1L;

  /** The HTTP status. */
  private final int status;

  /** The problem the server sent; transient because it holds JSON values. */
  private final transient Problem problem;

  ApiError(int status, Problem problem, Route route) {
    super(message(status, problem, route), null, route);
    this.status = status;
    this.problem = problem;
  }

  private static String message(int status, Problem problem, Route route) {
    return "crypto-service answered HTTP "
        + status
        + " ("
        + Redacted.printable(problem.type())
        + ") to "
        + route.method()
        + " "
        + route.path();
  }

  /**
   * The HTTP status.
   *
   * @return a status outside 2xx
   */
  public int status() {
    return status;
  }

  /**
   * The RFC 9457 problem.
   *
   * @return the problem; never null on an instance the client raised
   */
  public Problem problem() {
    return problem;
  }
}
