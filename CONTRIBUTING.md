<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

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

## Commits and pull requests

- Conventional commits (`feat:`, `fix:`, `docs:`, `test:`, `build:`,
  `ci:`, `chore:`), subject of at most 50 characters, body wrapped at 72
  saying what and why.
- Sign off every commit (`git commit -s`, see [DCO.txt](DCO.txt)).
- Pull requests go to the release branch `feat/v<next-version>`, never to
  `main` directly.
- Synthetic data only: never commit real keys, tokens or plaintexts.
