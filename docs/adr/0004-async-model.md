<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# ADR 0004: CompletableFuture first, blocking methods that wait

- Status: Accepted
- Date: 2026-11-15

## Context

Java callers range from reactive services to plain blocking code, and on
Java 21 virtual threads make blocking cheap. The SDK must offer
cancellation, per-attempt timeouts, an overall deadline and retries with
the same semantics as the other SDKs.

## Decision

- Every operation has `xxxAsync(...)` returning `CompletableFuture<T>`,
  built on `HttpClient.sendAsync`, and `xxx(...)` that waits for it with
  `get()`, unwrapping the cause.
- Cancelling the returned future cancels the in-flight exchange (which
  aborts it in the JDK client) and any scheduled retry. Interrupting a
  thread blocked in `xxx(...)` does the same, restores the interrupt
  flag and raises `TransportError`.
- Retries and the deadline run on one shared daemon timer thread that
  only starts non-blocking sends; no thread sleeps between attempts.
- Retries are off by default (`maxRetries = 0`). When on, they follow
  the TypeScript SDK's table: 429, 503, 504 and lost connections for
  idempotent routes; only 429 and 503 for routes that create or delete
  state (`/keygen`, `/keys/generate`, `/keys/delete`,
  `/kms/create-key`, `/opaque/register/`). `Retry-After` replaces the
  full-jitter wait and ends the retries when it exceeds `maxDelay`.

## Consequences

The client is safe to share across threads, async callers compose
futures, and blocking callers on virtual threads hold no platform thread
while they wait. The blocking stack trace starts in the HTTP client's
thread, not the caller's.
