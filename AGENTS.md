<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# AGENTS.md

`crypto-service-sdk-java` is the typed Java client for the Crypto Service
REST API (crypto-server). It holds no cryptography: the server generates,
holds and uses keys; this repository sends requests and maps responses to
records. It is proven by the shared conformance kit in `conformance/`.
These are the rules an automated change is most likely to break; humans
follow them too. Read [CONTRIBUTING.md](CONTRIBUTING.md) first.

## Versioning

- The version is `0.0.x` and moves **strictly by `0.0.1`**, in lockstep
  with the crypto-service suite. Never propose a `0.1.0` or `1.0` jump,
  and never bump it as a side effect of a change.
- `pom.xml` is the single source of truth. It is restated in
  `CryptoServiceClient.VERSION` (the User-Agent), `CITATION.cff`, the
  `SECURITY.md` and `SUPPORT.md` tables, every install snippet and a
  `## [0.0.N]` heading in `CHANGELOG.md`; `scripts/verify-release-versions`
  fails on any mismatch.
- Work for the next version happens on `feat/v<next-version>`, the only
  pull request into `main`; other branches merge into it, never into
  `main`.

## Thin client

- Never implement cryptography locally: no hashing, signing, encryption
  or key handling in `src/main`. Hex and base64 text encoding of what
  the API sends and receives is the only byte transformation allowed,
  and only the encoding the OpenAPI schema names.
- A new route becomes a typed method only when the pinned OpenAPI
  document and kit cover it; until then `request` reaches it.

## Secrets

- Never log anything. Never put credentials, keys, plaintexts or request
  or response bodies in exception messages, `toString` or test output;
  `ModelTest` checks every record's `toString`.
- Tests and examples use synthetic values only; no real keys or tokens
  anywhere in the repository.

## The gate

- `make check`: versions, whitespace format (library and examples within
  100 columns), Google Java Style (`make style`; `make format` applies
  it), the 500-line file limit and `mvn -B verify` (javac
  `-Xlint:all -Werror`, unit tests and fuzz seeds, JaCoCo at 100 % line
  and branch coverage, PMD ceilings: cyclomatic 10, cognitive 15, 60
  statements per method), plus the compile of every example and README
  snippet. `make regression` adds a live server: the kit, the examples,
  the README snippets and the benchmark smoke run. "It compiles" is not
  done; see [DEVELOPMENT.md](DEVELOPMENT.md).
- Every behaviour change lands with its test in the same commit.
- A dependency or plugin change needs `make lock` and an explanation in
  the commit body.

## Commits

- Conventional commits, imperative subject of at most 50 characters,
  body wrapped at 72 explaining what and why.
- Every commit is signed off (`git commit -s`) and SSH-signed. Never
  rewrite pushed history, never force-push, never publish to Maven
  Central or merge into `main` without the maintainer.

## Leave alone

- `conformance/` is vendored from crypto-service: never edit it. It
  changes only by copying the kit at a new `ci/server.ref` commit;
  `KitChecksumTest` checks it against `SHA256SUMS`.
- Generated output (`target/`, `site/`).
