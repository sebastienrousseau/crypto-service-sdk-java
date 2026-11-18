// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

/**
 * Fuzzes the problem-body parser: whatever an error response carries (a problem, a proxy's HTML,
 * truncated or hostile JSON), {@link Problem#parse} returns a complete problem and never throws,
 * and the {@link ApiError} built from it names only the status, problem type, method and path,
 * with no control character a log could take for a line break.
 *
 * <p>The seeds under {@code ProblemFuzzTestInputs/} run as regression tests in {@code mvn verify};
 * with {@code JAZZER_FUZZ=1} ({@code make fuzz}) it fuzzes.
 */
class ProblemFuzzTest {

  @FuzzTest(maxDuration = "60s")
  void anyErrorBodyBecomesAProblem(FuzzedDataProvider data) {
    int status = data.consumeInt(300, 599);
    byte[] body = data.consumeRemainingAsBytes();
    Problem problem = Problem.parse(body, status);
    assertNotNull(problem.type());
    assertNotNull(problem.title());
    assertNotNull(problem.detail());
    assertNotNull(problem.errors());
    assertNotNull(problem.extensions());
    assertNotNull(problem.toString());
    ApiError error = new ApiError(status, problem, Route.of("POST", "/v2/hash", true));
    assertEquals(status, error.status());
    assertEquals(
        "crypto-service answered HTTP "
            + status
            + " ("
            + Redacted.printable(problem.type())
            + ") to POST /v2/hash",
        error.getMessage());
    assertTrue(error.getMessage().chars().noneMatch(Character::isISOControl));
    assertTrue(problem.toString().chars().noneMatch(Character::isISOControl));
  }
}
