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
