<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Development

The single entry point for working on `crypto-service-sdk-java`: the
toolchain, every CI gate reproduced locally, the test layout and the
release model. [CONTRIBUTING.md](CONTRIBUTING.md) covers the
pull-request etiquette.

## Toolchain

| Tool | Version | Why |
| :--- | :--- | :--- |
| JDK | 17 or newer (CI: Temurin 17 and 21; `mise.toml`: 21) | The build floor is `maven.compiler.release` 17 |
| Maven | 3.10.1 via `./mvnw` (SHA-256 checked) | Any 3.9.0+ works |
| Node.js and pnpm | 24 and 11.9.0 (`mise.toml`) | Building and running the pinned crypto-server |
| Python | 3.12+ | The README snippet checker, the regression script, MkDocs |
| GNU Make | any | The task runner |
| Optional | `osv-scanner`, `reuse`, `codespell`, `lychee`, `vhs`, `actionlint` | The audit, licence, spelling, link, demo and workflow gates |

## Every CI gate, locally

| CI job | Local command | What it checks |
| :--- | :--- | :--- |
| CI / Lint and format | `scripts/verify-release-versions` and `make format-check size style lint` | Versions agree; no tabs, trailing spaces or CRLF; library and examples within 100 columns; 500 lines per file; every Java file as google-java-format writes it (Google Java Style); `javac -Xlint:all -Werror`; PMD ceilings |
| CI / Build, unit tests and coverage (Java 17, 21) | `make check` | `mvn verify`: unit tests, Jazzer seeds, JaCoCo 100 % line and branch, PMD; examples and README snippets compile |
| CI / Conformance against crypto-server | `make regression` | The full run below |
| CI / Benchmarks smoke | `make bench-smoke` | Every JMH scenario runs briefly; nothing asserted |
| CI / API breakage | `make api-check`, `make api-check-selftest` | japicmp against the last release on Maven Central; then proves the check still fails on a copy with a public class made package-private |
| Fuzz | `make fuzz` | Each Jazzer target for 60 s |
| Dependency audit | `make audit` | CycloneDX SBOM scanned by OSV-Scanner |
| Docs / Docs lint | `make manual`, `make docs-lint` | MkDocs strict build and Javadoc (doclint all); markdownlint, codespell, REUSE, lychee |
| CodeQL, Scorecard, DCO | (GitHub only) | Static analysis, supply-chain posture, `Signed-off-by` on every commit |
| Release (dry run) | `make release-dry-run` | The release artifacts, unsigned, without publishing |

## Regression run

`make regression` runs `scripts/regression.sh`, as the CI conformance
job does:

1. clones crypto-service at `ci/server.ref` into a temporary directory and
   builds it with `pnpm install --frozen-lockfile && pnpm -r run build`
   (skip with `CRYPTO_SERVER_DIR=/path/to/built/crypto-service`);
2. starts `node packages/crypto-server/dist/index.js` on a loopback port
   with a random `CRYPTO_API_KEY` and `JWT_SECRET` and
   `RATE_LIMIT_MAX=100000`, and waits for `/health`;
3. runs `make check`, `make conformance` (every vector, none skipped),
   `make examples`, `make readme-snippets` and `make bench-smoke`;
4. stops the server and deletes the clone, whether it passed or not.

## Tests

| Location | What | Needs a server |
| :--- | :--- | :--- |
| `ClientTest`, `RetryTest`, `ErrorModelTest` | Builder, headers, raw requests, retries, timeouts, deadline, cancellation, the error model, against loopback stub servers | No |
| `ModelTest`, `OperationsTest` | Every record (JSON round trip, byte accessors, redacted `toString`) and every typed operation, async and blocking | No |
| `KitChecksumTest` | `conformance/` matches `SHA256SUMS`; `ci/server.ref` is a full SHA | No |
| `ConformanceTest` | Every kit vector against `CRYPTO_SERVICE_URL`, through the typed methods where they apply | Yes (skipped by `mvn verify`, required by `make conformance`) |
| `src/fuzz/java/` | Jazzer: problem parser, success decoders and the `Retry-After` parser; seeds run in every build | No |
| `src/bench/java/` | JMH scenarios (profile `bench`) | `round_trip_hash` only |
| `examples/`, README snippets | Runnable programs that exit non-zero on a wrong answer | Yes |

### Regression tests

Every bug fix lands with a test that fails without the fix, in the same
commit, and the commit body says how it was seen to fail. Where no unit
test can observe the defect (a build or documentation gate), the fix
adds or extends the check that would have caught it, and the commit
says which one. A fix whose defect cannot be reproduced at all says so
and pins the intended behaviour instead. The fixes made before this
policy was written down have these tests:

| Fix | Regression test |
| :--- | :--- |
| `0986a9f` parse `Retry-After` seconds inside the `try` | `RetryAfterFuzzTest`: any header value yields a non-negative wait or none and never throws; its `two-points` seed fails with a `NumberFormatException` when the seconds pattern is loosened and the parse moved back outside the `try`. The original defect was not reachable through the pattern, so the test pins the contract. |
| `cc7c1c0` Javadoc links doclint rejected; `make conformance` without a JWT secret | The Docs workflow's `make manual` builds the Javadoc with doclint `all` and warnings as errors, which is the check that failed; `make conformance` and `KitAuth` refuse to run without `CRYPTO_SERVICE_JWT_SECRET`. |
| `aa90366` `make api-check` broken once a release existed | The CI API job runs `make api-check` on every push (it was red before the fix), then `make api-check-selftest`, which fails if the check stops rejecting a breaking change. |

### Coverage and complexity

JaCoCo measures the unit tests alone and `mvn verify` fails below 100 %
line and 100 % branch coverage of `src/main`, with no exclusions. PMD
enforces cyclomatic 10, cognitive 15 and 60 statements per method on
main and test code; `make size` enforces 500 lines per file. Halstead
difficulty (ceiling 30) has no maintained Java tool in the build and is
not measured.

The only lint exception is the one pain001-sdk-java has: in the `bench`
profile javac's `processing` category is off for test sources, because
the JMH annotation processor warns about the JUnit annotations.

## Release model

- Versions are `0.0.N` and move by `0.0.1`; `pom.xml` is the source of
  truth.
- Work happens on `feat/v<next-version>`, the only pull request into
  `main`.
- Preparing a release: replace `Unreleased` in the `## [0.0.N]` heading
  of `CHANGELOG.md` with the date, merge, then push the signed annotated
  tag `v0.0.N` with the message `crypto-service-sdk-java v0.0.N`.
- The tag starts `.github/workflows/release.yml`: it verifies the tag and
  every version, runs `make check`, builds the jars, CycloneDX SBOM and
  `SHA256SUMS`, attests build provenance, publishes to Maven Central from
  the `release` environment, then creates the GitHub release
  `crypto-service-sdk-java 0.0.N`. `workflow_dispatch` defaults to a dry
  run that publishes nothing.

### What the maintainer must set up (once)

| Where | Name | Value |
| :--- | :--- | :--- |
| Settings, Environments | `release` | Required reviewer: the maintainer; deployment rule: tags `v*` |
| `release` environment secret | `CENTRAL_TOKEN_USERNAME` | Central Portal user token name |
| `release` environment secret | `CENTRAL_TOKEN_PASSWORD` | Central Portal user token password |
| `release` environment secret | `GPG_PRIVATE_KEY` | ASCII-armoured private key that signs the Maven artifacts |
| `release` environment secret | `GPG_PASSPHRASE` | That key's passphrase |
| Central Portal | Namespace `co.crypto-service` | Verified for the publishing account |
| Settings, Pages | Source: GitHub Actions | Lets `docs.yml` deploy the manual |
