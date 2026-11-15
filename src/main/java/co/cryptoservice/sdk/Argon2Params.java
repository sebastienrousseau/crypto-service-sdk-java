// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * The {@code params} member.
 *
 * @param m memory in KiB
 * @param p lanes (parallelism)
 * @param t passes (time cost)
 */
public record Argon2Params(
    int m,
    int p,
    int t) {

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("Argon2Params")
        .show("m", m)
        .show("p", p)
        .show("t", t)
        .toString();
  }
}
