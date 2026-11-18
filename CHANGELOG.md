<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to the crypto-service suite's `0.0.x`
versioning: each release moves by `0.0.1`, in lockstep with the suite.

## [0.0.41] - Unreleased

### Fixed

- `ApiError` messages and the `toString` of `Problem` and
  `ValidationError` escape control characters and line separators in
  text the server chose, so a hostile server cannot forge log lines
  (CWE-117; security review findings F-01 and F-02).
- `make api-check` fails when it cannot fetch its baseline from Maven
  Central instead of passing (F-09).

### Changed

- The code follows the Google Java Style Guide, enforced by
  google-java-format (`make style`, `make format`); every file was
  reformatted once.
- CI and releases use exact Temurin builds (21.0.13+9, 17.0.21+9), and
  same-class Javadoc links carry explicit labels, so every release
  asset rebuilds bit for bit; `make repro-check` runs in CI and
  `make reproduce VERSION=...` checks a published release
  (`docs/REPRODUCING.md`).
- Every source file states its copyright (`SPDX-FileCopyrightText`).

### Added

- `docs/ASSURANCE.md`, the assurance case, and
  `docs/security-review-2026-11.md`, the first security review.
- Regression tests for earlier fixes: `RetryAfterFuzzTest` and
  `make api-check-selftest`; the regression-test policy in
  `DEVELOPMENT.md`; code review requirements in `CONTRIBUTING.md`.
- The README demo (`.github/demo.gif`), the release signing key's
  fingerprint and keyservers in `docs/packaging.md`, and a one-year
  roadmap with non-goals.

## [0.0.40] - 2026-11-16

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
