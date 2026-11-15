<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to the crypto-service suite's `0.0.x`
versioning: each release moves by `0.0.1`, in lockstep with the suite.

## [0.0.40] - Unreleased

### Highlights

- **Every v2 operation, async and blocking**: 59 v2 operations plus
  `/`, `/health`, `/live` and `/ready`, each as `xxxAsync` returning
  `CompletableFuture` and a blocking `xxx`, over one shared
  `java.net.http.HttpClient`.
- **The shared resilience contract**: per-attempt timeout, overall
  deadline, opt-in retries with full jitter and `Retry-After` following
  the TypeScript SDK's retry-safety table, cancellation that aborts the
  exchange, and `ApiError` / `TransportError` with RFC 9457 problems.
- **Proven against crypto-server**: the vendored conformance kit (119
  vectors) runs against a live server in CI, mostly through the typed
  methods; 100 % line and branch coverage, two Jazzer targets and JMH
  scenarios.

### Added

- `CryptoServiceClient` with its builder (`baseUrl`, `apiKey`, `token`,
  `timeout`, `deadline`, `retry`, `traceparent`, `autoTraceparent`,
  `responseListener`, `httpClient`) and the raw `request`.
- Request and result records for every operation, with `...Bytes()`
  accessors for hex and base64 members, `Bytes` helpers and enums for
  the closed request vocabularies.
- `Problem`, `ValidationError`, `ApiError`, `TransportError` and their
  base `CryptoServiceException`.
- The conformance kit from crypto-service at
  `3e8ecfd757e3577011953d8b47677f4c003426fb` (`ci/server.ref`), its
  runner, examples, README snippet checks, fuzz targets, benchmarks,
  CI, release workflow and documentation.

### Not included

- Typed methods for the deprecated v1 routes (sunset 2027-01-01) and
  `/metrics`; `request` reaches them.
