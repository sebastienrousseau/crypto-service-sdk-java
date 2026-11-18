<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Roadmap

What the SDK intends to do over the next year, to **November 2027**,
and what it will not do. Each item lands on the release branch with its
tests, and only when crypto-server and the conformance kit support it.
The suite's release cadence, not this page, sets the dates; the
maintainer reviews this page with every release and at least once a
quarter.

## Next (the next releases)

- Follow each crypto-service release: bump `ci/server.ref`, re-vendor
  the kit, and add or change every record and typed method whose schema
  changed, with tests.
- Publish benchmark results in `docs/BENCHMARKS.md` from `make bench` on
  a recorded machine, generated rather than typed.
- A limit on the size of a response body the client will buffer, so a
  misbehaving server cannot exhaust the caller's memory.

## Within the year (to November 2027)

- Drop the deprecated v1 routes from the documentation once crypto-server
  removes them (sunset 2027-01-01); they were never typed methods.
- Streaming request bodies for large plaintexts, if crypto-server adds
  streaming routes.
- Keep every release reproducible and its reproduction documented
  ([reproducing a release](REPRODUCING.md)), moving the pinned JDK build
  forward with each Temurin update.
- Look for a second maintainer with release rights, so the continuity
  items in [governance](governance.md) can be arranged.
- A Halstead-difficulty check in CI, if a maintained Java tool appears
  (it is the one complexity ceiling not measured today).

The Java floor stays at 17 for the whole year.

## Non-goals

The SDK will not:

- **implement cryptography or work offline.** No local hashing,
  signing, encryption or key handling, and no fallback when the server
  cannot be reached; the server does all of it
  ([ADR 0001](adr/0001-thin-client-over-the-server.md)).
- **hold or cache keys or secrets** beyond the credentials the caller
  configures, or write anything to disk.
- **log.** It has no logging dependency and writes no log; callers
  observe responses through `responseListener`.
- **offer typed methods for the deprecated v1 routes or `/metrics`**;
  `request` reaches them.
- **add HTTP or reactive libraries.** The JDK's `HttpClient` and
  `CompletableFuture` are the stack
  ([ADR 0002](adr/0002-http-stack.md),
  [ADR 0004](adr/0004-async-model.md)); a caller can inject its own
  `HttpClient`.
- **support Java older than 17**, or Android.
- **follow redirects, or offer a switch that disables TLS
  verification.**
