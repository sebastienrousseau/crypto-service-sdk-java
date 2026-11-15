<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# ADR 0001: A thin client over crypto-server

- Status: Accepted
- Date: 2026-11-15

## Context

Crypto Service exists so that applications can use classical and
post-quantum cryptography without holding keys or carrying a
cryptographic library: crypto-server generates keys, keeps private keys
server-side and performs every operation. A Java SDK could also bundle
algorithms (Bouncy Castle, the JDK's providers) to work offline or to
save a round trip.

## Decision

The SDK implements no cryptography. Every operation is one HTTP call to
crypto-server; the SDK encodes the request, sends it and maps the answer
to a record. Text encodings (hex, base64) are the only transformation it
applies to bytes, and only the ones the OpenAPI schema names.

The contract is crypto-service's OpenAPI document and conformance kit at
the commit in `ci/server.ref`. The kit is vendored byte for byte and
every vector runs against a live server in CI.

## Consequences

- One implementation of each algorithm, audited and tested in one place,
  and private keys that never reach the client process.
- Nothing works without a reachable server, and every operation costs a
  round trip. The README says so under "When not to use".
- A server upgrade can change behaviour; the pinned commit and the kit
  make that a reviewed change rather than a surprise.
