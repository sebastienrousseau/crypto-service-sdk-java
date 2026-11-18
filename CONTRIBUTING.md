<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Contributing

Thanks for looking. This SDK is a thin client: it sends requests to the
Crypto Service REST API (crypto-server) and maps the responses to Java
records. The cryptography lives in the server, in
[`crypto-service`](https://github.com/sebastienrousseau/crypto-service);
change it there.

## Before you open a pull request

Everything CI checks you can run locally with a JDK 17 or newer;
`./mvnw` fetches the pinned Maven and checks its SHA-256:

```sh
make check
```

It runs the version check, the whitespace and size gates, `mvn -B
verify` (unit tests against loopback stub servers, fuzz seeds, JaCoCo at
100 %, PMD) and compiles the examples and README snippets.

To run the conformance kit, start crypto-server with an API key and a
JWT secret (synthetic values) and point the runner at it:

```sh
make conformance
```

with `CRYPTO_SERVICE_URL`, `CRYPTO_SERVICE_API_KEY` and
`CRYPTO_SERVICE_JWT_SECRET` set. `make regression` does all of it for
you: it builds the pinned server, starts it on a loopback port with
random credentials and runs every live check.

Maven refuses any dependency or plugin whose SHA-256 is not recorded in
`.mvn/checksums/`. After changing a version, including a Dependabot
bump, run `make lock` and commit the diff with the change.

## Coding style

The project follows the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Layout is not a matter of taste here: every Java file (library, tests,
fuzz targets, benchmarks and examples) must be exactly what
google-java-format writes, the version bundled with the pinned
`fmt-maven-plugin` in `pom.xml`. `make format` rewrites the files and
`make style` checks them; CI's Lint and format job runs `make style`.

On top of the guide, the build enforces:

- no tabs, trailing spaces or CRLF, a final newline, and at most 100
  columns in the library and examples (`make format-check`,
  `.editorconfig`);
- at most 500 lines per Java file (`make size`);
- `javac -Xlint:all -Werror`: a new compiler warning fails the build;
- PMD ceilings per method: cyclomatic complexity 10, cognitive
  complexity 15, 60 statements (`config/pmd/complexity.xml`);
- Javadoc on every public type and member, checked by doclint `all`
  with warnings as errors (`make javadoc`).

The parts of the guide no tool checks (naming, the order of members,
Javadoc wording) are checked in review. The rules that are particular
to this SDK, such as no cryptography in `src/main` and no secrets in
any message or `toString`, are in [AGENTS.md](AGENTS.md).

## Updating the server contract

1. Put the new crypto-service commit SHA in `ci/server.ref`.
2. Copy that commit's `conformance/` directory here unchanged.
3. Compare its `openapi/crypto-service.openapi.yaml` with the records and
   operations; add or change them with tests.
4. Run `make regression`.

## The README demo

`.github/demo.gif` is rendered from `.github/demo.tape` by `make demo`
with [vhs](https://github.com/charmbracelet/vhs), against a server at
`CRYPTO_SERVICE_URL`; never record it by hand.

## Code review

Every change reaches `main` through a pull request: topic branches merge
into the release branch `feat/v<next-version>`, and its one pull request
merges into `main`. Branch protection on `main` requires signed commits,
forbids force-pushes and deletion, applies to administrators too, and
requires these checks to pass: Lint and format, Build, unit tests and
coverage (Java 17 and 21), Benchmarks smoke run, API breakage,
Conformance against crypto-server, both CodeQL analyses, Fuzz
ProblemFuzzTest and ResponseDecodingFuzzTest, the dependency audit, the
manual build, markdownlint, codespell, REUSE, links and Signed-off-by
on every commit. The other jobs (Fuzz RetryAfterFuzzTest, Reproducible
build) run on every pull request too, and a red one blocks
the merge by the maintainer's rule rather than by the setting.

### What a reviewer checks

The checks CI cannot make, in this order:

1. **The change does what it says.** Read the diff against the
   description and, for an API change, against the pinned OpenAPI
   document and the conformance kit.
2. **Tests.** Behaviour changes come with tests in the same commit; a
   bug fix comes with a test the commit says was seen to fail without
   the fix ([DEVELOPMENT.md](DEVELOPMENT.md#regression-tests)).
3. **The thin-client and secrets rules** in [AGENTS.md](AGENTS.md): no
   cryptography in `src/main`, nothing secret in a message or
   `toString` (a new record with secret members redacts them), no
   logging, synthetic data only.
4. **Retry safety.** A new route that creates or deletes server state is
   in the `Route` table.
5. **Compatibility.** A change to the public API, to what is sent, or to
   which exception an answer raises is called out in `CHANGELOG.md`;
   japicmp catches the binary and source breaks.
6. **Dependencies and CI.** A new or bumped dependency or plugin comes
   with `make lock` and a reason; a workflow change keeps actions pinned
   by SHA, permissions minimal and `persist-credentials: false`.
7. **The assurance case.** A change to a trust boundary, entry point or
   release step updates [docs/ASSURANCE.md](docs/ASSURANCE.md).
8. **Style and docs.** Google Java Style (enforced), Javadoc on public
   API, documentation and changelog current.

A pull request is acceptable when every required check passes, every
commit is signed off and signed, and the reviewer has no open question
on the list above. A reviewer records what they checked in the review
or the pull request; "LGTM" alone is not a review.

### Review with a single maintainer

The project has one maintainer, Sebastien Rousseau
([GOVERNANCE.md](GOVERNANCE.md)). Pull requests from anyone else are
reviewed by the maintainer against the list above before merging. The
maintainer's own pull requests, including those written with AI
assistance, have no second human reviewer: the maintainer reviews the
diff against the same list, the pull request description says what was
run and observed, and the required checks must pass; the maintainer
then merges it himself. Branch protection therefore requires zero
approving reviews. This is self-review, not two-person review, and the
project does not claim otherwise. When a second maintainer joins,
`main` will require an approving review from someone other than the
author.

## Commits and pull requests

- Conventional commits (`feat:`, `fix:`, `docs:`, `test:`, `build:`,
  `ci:`, `chore:`), subject of at most 50 characters, body wrapped at 72
  saying what and why.
- Sign off every commit (`git commit -s`, see [DCO.txt](DCO.txt)); the
  DCO workflow checks every commit of a pull request. Commits to `main`
  must also carry a verified signature (SSH or GPG): set
  `commit.gpgsign` and a signing key. The maintainer's keys are in
  [KEYS.asc](KEYS.asc).
- Pull requests go to the release branch `feat/v<next-version>`, never to
  `main` directly.
- Synthetic data only: never commit real keys, tokens or plaintexts.
