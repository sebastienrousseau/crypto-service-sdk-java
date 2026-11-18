// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * The {@code params} member.
 *
 * @param N the scrypt cost, or null
 * @param info the HPKE info, or null
 * @param iterations the PBKDF2 iterations, or null
 * @param p the scrypt parallelism, or null
 * @param r the scrypt block size, or null
 */
public record KdfParams(Integer N, String info, Integer iterations, Integer p, Integer r) {

  /**
   * A copy with {@code N} replaced.
   *
   * @param N the scrypt cost, or null
   * @return the new request
   */
  public KdfParams withN(Integer N) {
    return new KdfParams(N, info, iterations, p, r);
  }

  /**
   * A copy with {@code info} replaced.
   *
   * @param info the HPKE info, or null
   * @return the new request
   */
  public KdfParams withInfo(String info) {
    return new KdfParams(N, info, iterations, p, r);
  }

  /**
   * A copy with {@code iterations} replaced.
   *
   * @param iterations the PBKDF2 iterations, or null
   * @return the new request
   */
  public KdfParams withIterations(Integer iterations) {
    return new KdfParams(N, info, iterations, p, r);
  }

  /**
   * A copy with {@code p} replaced.
   *
   * @param p the scrypt parallelism, or null
   * @return the new request
   */
  public KdfParams withP(Integer p) {
    return new KdfParams(N, info, iterations, p, r);
  }

  /**
   * A copy with {@code r} replaced.
   *
   * @param r the scrypt block size, or null
   * @return the new request
   */
  public KdfParams withR(Integer r) {
    return new KdfParams(N, info, iterations, p, r);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KdfParams")
        .show("N", N)
        .hide("info", info)
        .show("iterations", iterations)
        .show("p", p)
        .show("r", r)
        .toString();
  }
}
