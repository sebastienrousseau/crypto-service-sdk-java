<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Security review, November 2026

| Item | Value |
| :--- | :--- |
| Date | 2026-11-18 |
| Reviewer | Sebastien Rousseau, with AI-assisted analysis (Claude) |
| Version reviewed | `main` at `21782e8` (0.0.40 plus the 0.0.41 branch so far), and the fixes below |
| Against | The [assurance case](ASSURANCE.md): requirements R1 to R5, the trust boundaries and the weakness table |
| Result | Three low-severity defects found and fixed (F-01, F-02, F-09); six findings accepted with reasons |

## Scope

- **Library code**: every class in `src/main/java/co/cryptoservice/sdk/`,
  with most attention on `CryptoServiceClient` (builder and validation),
  `Transport`, `Exchange`, `Backoff`, `Route`, `Operation`, `Decoder`,
  `Json`, `Problem`, `ApiError`, `TransportError`, `Redacted`,
  `Traceparent`, `RawResponse` and `ValidationError`.
- **Transport and TLS**: the `HttpClient` the SDK builds, the injected
  client path, redirects, the scheme, timeouts and the deadline.
- **Credential handling**: where the API key and JWT are held, which
  requests carry them, and every string that could print them.
- **Input parsing**: problem bodies, success bodies, `Retry-After`,
  `Content-Type`, and caller input to the builder and the raw `request`.
- **Error paths**: what each failure raises, what its message says, and
  whether a future can be left uncompleted.
- **CI workflows and permissions**: all nine workflows under
  `.github/workflows/`.
- **Release pipeline**: `release.yml`, the tag checks, the `release`
  environment, signing and the published assets.
- **Dependency set**: the runtime dependency tree and the build plugins.

Out of scope: crypto-server and its cryptography (reviewed in
crypto-service), GitHub's and Maven Central's own infrastructure.

## Method

1. Read every class in scope line by line against the assurance case,
   following each entry point to where it leaves the SDK.
2. For each weakness row of the assurance case, found the code and the
   test that support it, and tried to break the claim: for example a
   problem `type` holding CR/LF, a raw path of `//evil.example/x`, a
   `Retry-After` of `1.2.3`, a closed `HttpClient` under a pending retry
   (the JDK returns a failed future, so the call still completes).
3. Wrote a failing test for each suspected defect before changing code;
   kept a fix only once the test passed with it.
4. Checked every workflow for its permissions, pinning, checkout
   credentials and expression use in `run:` steps; `actionlint` reports
   nothing.
5. Ran `make audit` (OSV-Scanner over the CycloneDX SBOM, 16 packages
   including test scope): no known vulnerability. Fuzzed the new
   `RetryAfterFuzzTest` for 60 seconds with no finding; the other two
   targets' seeds replay in every build and CI fuzzes them on every
   pull request.
6. Rebuilt the 0.0.40 release and compared it with what was published
   ([reproducing a release](REPRODUCING.md)).

## Findings

Severity is for a caller of the SDK: **High** (secret disclosure or
integrity loss reachable by a network attacker), **Medium**, **Low**
(needs a hostile server or an unusual configuration, limited impact),
**Info** (hardening, no defect).

| ID | Severity | Finding | Disposition |
| :--- | :--- | :--- | :--- |
| F-01 | Low | **Log forging through problem members (CWE-117).** `ApiError`'s message and `Problem.toString` quoted the server's `type`, `title`, `instance` and `code` verbatim. A hostile server could put CR/LF or U+2028 in them and forge lines in any log that records the exception, which the SDK documents as safe to log. | **Fixed** in `51da109`: `Redacted.printable` escapes control characters and line separators in every shown value and in the message. `ErrorModelTest.serverTextCannotForgeLogLines` failed before the fix; `ProblemFuzzTest` now asserts no control character reaches either string. |
| F-02 | Low | **The same through `ValidationError.toString` (CWE-117).** The record kept its default `toString`, printing the server's `field` and `message` verbatim. | **Fixed** in `c6ff4af`: it now uses `Redacted.show`. The new assertion failed on the default `toString`. |
| F-03 | Low | **Response bodies are buffered whole (CWE-770).** Every response is read with `BodyHandlers.ofByteArray()`, with no size limit, so a hostile or broken server can make a call hold an arbitrarily large body in memory. Jackson's stream read constraints bound nesting and token sizes, not the body. | **Accepted for now**, on the [roadmap](ROADMAP.md): the server is the caller's own deployment, inside its trust for availability, and a limit needs an API decision (a default and a builder option). |
| F-04 | Low | **A slow body is bounded only by the deadline.** The per-attempt `timeout` covers connecting and the response headers, as its Javadoc says; a body that trickles in never times out unless the caller sets `deadline` or cancels the future. | **Accepted, documented**: `deadline` and cancellation exist for this, and the Javadoc of `timeout` says what it covers. |
| F-05 | Low | **Cleartext http is allowed to any host (CWE-319).** The builder accepts `http://` for any host, so a caller can send credentials in cleartext off loopback. | **Accepted, documented** (README *Security*, SECURITY.md): loopback and in-cluster sidecar deployments use http, and refusing it would break them. Revisit with the suite (a loopback-only default would be a breaking change). |
| F-06 | Info | **The release build job runs the full gate with `id-token: write` and `attestations: write`.** Any build plugin or test runs while the job can mint an OIDC token. | **Accepted**: every plugin and dependency is pinned by SHA-256 and every action by commit; the Central token and GPG key are not in that job. Moving the attestation to a job that only hashes the uploaded assets would narrow it; listed as a possible task. |
| F-07 | Info | **The tag signature is checked against the `KEYS.asc` of the tagged tree.** Someone able to push to `main` could add a key and sign a tag with it in the same change. | **Accepted**: `main` takes only signed commits through a pull request with the required checks, and publishing also needs the `release` environment's required reviewer. |
| F-08 | Info | **Credentials are held as `String`s** in `Transport` for the client's lifetime and cannot be wiped. | **Accepted**: the JDK's `HttpRequest` headers are strings anyway; the SDK never prints them (`ClientTest.builderToStringRedactsCredentials`). |
| F-09 | Low | **`make api-check` passed when it could not fetch its baseline.** It discarded curl's error and treated an empty answer as "no release yet", so a network or Central failure turned the API breakage gate into a pass. | **Fixed** in `1f6af75`: it now fails; `make api-check-selftest` checks both that a breaking change and a missing baseline fail it, and it failed on the old recipe. |

No finding reached Medium or High. Checked and found sound, with the
tests that hold them: redirects refused (`ClientTest.redirectsAreRefused`),
no TLS bypass, base URL and header validation, credentials only on
authenticated routes, credentials never in a message or `toString`
(`ModelTest`), the retry-safety table, bounded retries and
`Retry-After`, deserialisation into fixed records only, a raw path
cannot change the host (`ClientTest.rawPathCannotChangeTheHost`, added
by this review), and no expression interpolation in workflow `run:`
steps.

## Follow-up

- F-03: a response size limit, on the roadmap.
- F-06: a separate attestation job, recorded as a possible task.
- The next review is due with the first release after 2027-05-18, or
  sooner if a trust boundary changes.
