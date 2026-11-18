<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Assurance case

Why `crypto-service-sdk-java` meets its security requirements: what it
protects, from whom, where the trust boundaries are, how secure design
principles are applied, and how the common weaknesses of an HTTP client
library are countered. Every claim points at the code, test or workflow
that makes it true; the [2026-11 security review](security-review-2026-11.md)
checked them. [SECURITY.md](../SECURITY.md) has the short version and
how to report a vulnerability.

## Security requirements

The SDK is a thin HTTP client for crypto-server. It implements no
cryptography ([ADR 0001](adr/0001-thin-client-over-the-server.md)). Its
security requirements are therefore about the client, not the algorithms:

1. **R1 Credentials reach only the configured server**, over the
   transport the caller chose, and never another origin.
2. **R2 No secret leaks through the SDK's own output**: exception
   messages and `toString` carry no credential, key, plaintext,
   password, signature, ciphertext or body, and no text a server chose
   can forge a log line through them. The SDK writes no log and no file.
3. **R3 A hostile or broken server cannot make the client misbehave**:
   any response yields a typed result, an `ApiError` or a
   `TransportError`, never an unexpected exception, a partial result,
   or unbounded retries.
4. **R4 Retries never repeat a state change** the server may already
   have made.
5. **R5 What users download is what this repository built**: pinned,
   checksum-verified inputs, signed tags and artifacts, provenance, and
   reproducible builds.

## Threat model

### Assets

| Asset | Where it lives |
| :--- | :--- |
| API key and JWT | The caller's configuration; held in `Transport`, sent as `x-api-key` and `Authorization: Bearer` |
| Request payloads | Plaintexts, passwords, messages to sign, key identifiers, exported or wrapped key material the caller sends |
| Response payloads | Ciphertexts, decrypted plaintexts, shared secrets, exported keys, signatures and verification verdicts |
| Integrity of results | A verdict such as `VerifyResult.valid()` must be the server's answer, not something the client made up |
| The caller's process | Threads, memory and the caller's logs |
| The published artifacts | The jars on Maven Central and the GitHub release, and the release signing key and Central token that make them |

### Actors

| Actor | Trust | What they can do |
| :--- | :--- | :--- |
| The caller (application developer) | Trusted for its own configuration | Chooses the base URL, credentials, timeouts, retries, an injected `HttpClient`, listeners |
| crypto-server and its operator | Trusted for cryptographic results; its responses are still parsed as untrusted input | Answers every call |
| Network attacker | Untrusted | Reads or alters traffic that is not TLS-protected; redirects; injects responses |
| Hostile or compromised server, or a proxy in between | Untrusted for robustness | Sends malformed, huge, slow or adversarial responses and headers |
| Log reader | Untrusted | Reads whatever the caller logs, including exception messages |
| Malicious contributor or compromised dependency, plugin or action | Untrusted | Tries to get code into the build or the release |

### Entry points

- The public API: `CryptoServiceClient.Builder`, every typed operation,
  the raw `request`/`requestAsync`, `Bytes`, and the records' factories.
- HTTP responses: status, headers (`Retry-After`, `Content-Type`) and
  body, for typed and raw calls.
- Caller-supplied code the SDK calls: an injected `HttpClient`, the
  `ResponseListener`, a traceparent value.
- The build and release pipeline: `pom.xml`, `.mvn/`, the Maven wrapper,
  GitHub Actions workflows, `ci/server.ref`, release tags.

The SDK reads no environment variable, system property or file at run
time (the examples and tests do).

## Trust boundaries

```text
 caller code ──(1)──► SDK ──(2)──► network ──(3)──► crypto-server
                       ▲
 Maven Central ──(4)───┘ (dependencies at build time; the SDK itself for users)
 GitHub Actions ──(5)──► release: Maven Central, GitHub release
```

| Boundary | What crosses it | How it is guarded |
| :--- | :--- | :--- |
| **(1) caller → SDK** | Configuration and request records | The builder validates the base URL (http or https, a host, no user info, query or fragment), positive durations, header-safe credentials and the traceparent, and refuses an injected `HttpClient` that follows redirects (`CryptoServiceClient.Builder.build`, `normalise`, `checkHeaders`, `checked`; `ClientTest.builderRejectsBadSettings`, `injectedHttpClientMustNotFollowRedirects`). A raw path must start with `/` and cannot leave the base URL (`ClientTest.rawRequestChecksItsArguments`, `rawPathCannotChangeTheHost`). |
| **(2) SDK → network** | Requests with credentials; responses | The JDK `HttpClient` with the JVM's trust store and hostname verification; the SDK exposes no switch to weaken them and never follows redirects (`defaultHttpClient`; `ClientTest.redirectsAreRefused`). https is the caller's choice of base URL (see F-05 in the review). |
| **(3) network → SDK** | Status, headers, bodies from the server or anything in between | Every response passes `Operation.result`: non-2xx becomes `ApiError` with `Problem.parse` (never throws), a 2xx is decoded or becomes `TransportError` without quoting it; `Retry-After` is parsed defensively. Fuzzed by `ProblemFuzzTest`, `ResponseDecodingFuzzTest` and `RetryAfterFuzzTest`. |
| **(4) Maven Central → build; SDK → users** | Plugins and dependencies; the published SDK | Every resolved artifact must match `.mvn/checksums/` (`.mvn/maven.config`); `./mvnw` checks the Maven distribution's SHA-256; the enforcer bans unpinned plugins and dynamic versions; OSV-Scanner audits the SBOM; users verify GPG signatures, `SHA256SUMS`, provenance, or rebuild ([packaging](packaging.md), [reproducing a release](REPRODUCING.md)). |
| **(5) CI → release** | Code from pull requests; the release credentials | Actions pinned by commit SHA; workflows default to `contents: read` or `permissions: {}`; `persist-credentials: false` on every checkout; `ci/server.ref` is checked to be a 40-hex SHA before use; the release needs a signed annotated tag verified against `KEYS.asc`, and its secrets live only in the `release` environment, used by the publish job alone. |

## Secure design principles

How the Saltzer and Schroeder principles apply here, with the code that
carries each one.

| Principle | Applied as |
| :--- | :--- |
| **Economy of mechanism** | No cryptography in the client; one runtime dependency (Jackson databind); one Jackson configuration (`Json`); one transport (`Transport`, `Exchange`); one timer thread (`Scheduler`). Complexity ceilings (PMD: cyclomatic 10, cognitive 15) and 100 % line and branch coverage keep each piece small and fully exercised. |
| **Fail-safe defaults** | Retries off (`RetryOptions.NONE`); redirects never followed; TLS verification always on; a 30-second per-attempt timeout; a body that is not a problem becomes an `about:blank` problem; a 2xx that does not decode, or decodes to nothing, is a `TransportError`, never a partial or default result (`Decoder.fromReader`, `ErrorModelTest.malformedSuccessBodyIsATransportErrorWithoutTheBody`). |
| **Complete mediation** | Every attempt's request is built in one place, `Transport.request`, which attaches credentials only for authenticated routes; every response goes through `Operation.result`; the closed flag is checked on every call (`Transport.exchange`; `ClientTest.closedClientRefusesCalls`). |
| **Open design** | Everything is public: the code, the OpenAPI contract and the conformance kit the CI runs against a live server; no security property depends on the code being secret. |
| **Separation of privilege** | Publishing needs a signed tag, the `release` environment (with its required reviewer), the Central token and the GPG key. With a single maintainer the same person holds all of them; see [governance](governance.md). |
| **Least privilege** | Public routes (`/health`, `/live`, `/ready`, OPAQUE login) carry no credentials (`MetaOperations`, `ServiceOperations`; `ClientTest.publicRoutesCarryNoCredentialsAndGetsNoBody`); workflow tokens are read-only except where a job needs more, and only the publish job sees the release secrets. |
| **Least common mechanism** | A client shares nothing mutable with another client: its `Transport` is immutable apart from an `AtomicBoolean`; the shared `Json.MAPPER` is configured once and thread-safe; the shared timer only completes futures or starts non-blocking sends. |
| **Psychological acceptability** | The safe path is the short one: a builder with secure defaults, typed records, `Bytes` helpers for the encodings, and one exception hierarchy. Nothing has to be turned on to be safe. |

## Common weaknesses countered

The CWE Top 25 (2024) entries and OWASP items that apply to an HTTP
client library, and what counters each.

| Weakness | Countered by | Evidence |
| :--- | :--- | :--- |
| **CWE-295** Improper certificate validation | The JDK `HttpClient`'s default trust store and hostname verification; no option to disable them. An injected client's TLS settings are documented as the caller's. | `CryptoServiceClient.Builder.defaultHttpClient`, `httpClient` Javadoc |
| **CWE-522 / CWE-200** Credentials exposed to another origin | Redirects never followed; an injected client that follows them is refused; a base URL with user info is refused; a raw path cannot change the host. | `ClientTest.redirectsAreRefused`, `injectedHttpClientMustNotFollowRedirects`, `builderRejectsBadSettings`, `rawPathCannotChangeTheHost` |
| **CWE-319** Cleartext transmission | https is supported and recommended off loopback; http is allowed for loopback and in-cluster use. Accepted, documented (review F-05). | README *Security*, SECURITY.md |
| **CWE-532 / CWE-209** Secrets in logs or error messages | Messages name only status, problem type, method and path; every record's `toString` redacts secret members; the builder's `toString` redacts credentials; a decoding failure carries no parser cause. | `Redacted`, `ApiError`, `TransportError.malformed`; `ModelTest.toStringHidesSecretsAndNamesEveryMember`, `ClientTest.builderToStringRedactsCredentials` |
| **CWE-117** Log forging through server text | Server-chosen strings in messages and `toString` have control characters and line separators escaped. | `Redacted.printable`; `ErrorModelTest.serverTextCannotForgeLogLines`; `ProblemFuzzTest` |
| **CWE-20 / CWE-113** Improper input validation, header injection | Base URL, durations, traceparent and credentials are checked at build time; credentials with CR/LF are refused by the JDK's header validation before any request. | `ClientTest.builderRejectsBadSettings`, `traceparentIsValidated` |
| **CWE-502** Deserialisation of untrusted data | Jackson binds only to the SDK's records and `JsonNode`; default typing is never enabled. | `Json` |
| **CWE-400 / CWE-770** Uncontrolled resource consumption | Jackson's default stream read constraints bound nesting and token sizes; retries are bounded by `maxRetries`, jittered, capped by `maxDelay` and the deadline. The response body is buffered whole (review F-03) and only the deadline bounds a slow body (F-04). | `Backoff`, `Exchange.retryOr`; `RetryTest` |
| **CWE-248 / CWE-754** Uncaught exceptions on unexpected input | Problem parsing never throws; decoders fail only with `IOException`, mapped to `TransportError`; an unparsable `Retry-After` is ignored. All three are fuzzed with seeds that replay in every build. | `ProblemFuzzTest`, `ResponseDecodingFuzzTest`, `RetryAfterFuzzTest` |
| **CWE-362** Race conditions | The client is immutable after `build()`; an `Exchange` settles once (`result.isDone()`); late outcomes are dropped; cancellation aborts the exchange. | `Exchange`; `RetryTest.cancellingTheFutureAbortsTheExchange`, `interruptingABlockingCallCancelsIt` |
| Duplicate state changes (OWASP API: unsafe retries) | The retry-safety table: key generation and deletion, KMS key creation and OPAQUE registration are retried only after 429 or 503. | `Route`; `RetryTest.retrySafetyTableMatchesTheTypeScriptSdk`, `statefulRouteIsNotRetriedAfter504` |
| **CWE-918** Requests to an unintended host | Requests go only to the configured base URL; typed routes are constants; the raw path is appended, never resolved. | `Transport.request`; `ClientTest.rawPathCannotChangeTheHost` |
| **CWE-798** Hard-coded credentials | None in the code; tests and examples use synthetic values; GitHub secret scanning and push protection are on. | `src/test/java/co/cryptoservice/sdk/Synthetic.java` |
| **OWASP A06 / CWE-1395** Vulnerable or outdated components | One runtime dependency; Dependabot weekly; OSV-Scanner over the CycloneDX SBOM on every push and pull request. | `.github/workflows/audit.yml`, `.github/dependabot.yml` |
| **OWASP A08 / CWE-494 / CWE-829** Software and data integrity | Checksum-verified dependencies and Maven; actions pinned by SHA; signed tags; GPG-signed artifacts; provenance attestations; reproducible builds checked in CI. | `.mvn/`, `.github/workflows/release.yml`, `make repro-check` |
| **CWE-78 / CWE-94** Injection into CI | `ci/server.ref` is validated as a full SHA before `git fetch`; workflow inputs reach shell steps through environment variables, not expression interpolation. | `.github/workflows/ci.yml`, `release.yml` |

## What is not claimed

- Cryptographic correctness: the server's, assured in
  [crypto-service](https://github.com/sebastienrousseau/crypto-service).
- Protection of secrets in the caller's own memory, heap dumps or logs
  of values the caller reads from results.
- Confidentiality over http: the caller chooses the scheme.
- Availability against a hostile server beyond the bounds listed above
  (F-03, F-04).
- A second person's review of changes: there is one maintainer
  ([CONTRIBUTING](../CONTRIBUTING.md#code-review)).

## Keeping it true

A change that moves a trust boundary (a new entry point, dependency,
workflow permission or release step) updates this page in the same
pull request; reviewers check it ([CONTRIBUTING](../CONTRIBUTING.md#code-review)).
Each security review re-checks every row against the code.
