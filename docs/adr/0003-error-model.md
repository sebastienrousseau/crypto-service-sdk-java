<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# ADR 0003: ApiError and TransportError under one base

- Status: Accepted
- Date: 2026-11-15

## Context

crypto-server answers every error with an RFC 9457 problem
(`application/problem+json`): `type` (`urn:crypto-service:problem:<slug>`),
`title`, `status`, `detail`, `instance`, and extensions such as `code`
(`AUTH_FAILED`, `KEY_NOT_FOUND`) and `errors[]` for validation failures.
All seven Crypto Service SDKs share one error model.

## Decision

- `ApiError`: the server answered outside 2xx. It carries the status and
  the parsed `Problem`. A body that is not a JSON object with a string
  `detail` (a proxy page, an empty body, a refused redirect) becomes an
  `about:blank` problem for the status, so the shape is always the same.
- `TransportError`: no usable answer. `timedOut()` is true for an attempt
  timeout or the overall deadline. A 2xx that does not decode is also a
  `TransportError`, without the parser's exception, whose message could
  quote the body.
- Both extend the sealed, unchecked `CryptoServiceException`, which
  exposes the method and path. The names follow the shared model; they
  are exceptions, not `java.lang.Error`s.
- Messages name the status, problem type, method and path only.
  `Problem.toString` redacts `detail`. Async calls complete their future
  exceptionally with the same exceptions; blocking calls throw them
  unwrapped.

## Consequences

Callers branch on `status()` and `problem().code()`, and can log any
exception without leaking request content.
