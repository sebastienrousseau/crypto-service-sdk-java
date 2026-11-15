// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

/**
 * The {@code metadata} member.
 *
 * @param exp the expiry, or null
 * @param kid the key identifier
 * @param use {@code sig} or {@code enc}, or null
 */
public record KeyMetadata(
    String exp,
    String kid,
    String use) {

  /**
   * A copy with {@code exp} replaced.
   *
   * @param exp the expiry, or null
   * @return the new request
   */
  public KeyMetadata withExp(String exp) {
    return new KeyMetadata(exp, kid, use);
  }

  /**
   * A copy with {@code kid} replaced.
   *
   * @param kid the key identifier
   * @return the new request
   */
  public KeyMetadata withKid(String kid) {
    return new KeyMetadata(exp, kid, use);
  }

  /**
   * A copy with {@code use} replaced.
   *
   * @param use {@code sig} or {@code enc}, or null
   * @return the new request
   */
  public KeyMetadata withUse(String use) {
    return new KeyMetadata(exp, kid, use);
  }

  /**
   * Names the members; values that may be secret (keys, plaintexts, passwords, signatures) are
   * redacted.
   *
   * @return a description safe to log
   */
  @Override
  public String toString() {
    return new Redacted("KeyMetadata")
        .show("exp", exp)
        .show("kid", kid)
        .show("use", use)
        .toString();
  }
}
