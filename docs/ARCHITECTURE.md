<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Architecture

`crypto-service-sdk-java` is a thin, typed client for the Crypto Service
REST API (crypto-server). This page is the map for contributors: what
each part does, how a call flows, and where the guarantees are enforced.

## The shape

```text
caller ──► CryptoServiceClient (typed xxxAsync / xxx methods)
             │  ClientCore.call: encode the request record (Json), start an Exchange
             ▼
           Transport ──► Exchange ──► java.net.http.HttpClient ──► crypto-server
             │  per attempt: headers, credentials, timeout, traceparent
             │  Backoff: retry decision and wait; Scheduler: retries and deadline
             ▼
           Operation.result: 2xx ──► Decoder ──► result record
                             else ──► ApiError(Problem.parse(body))
           no response / bad body ──► TransportError
```

There is no cryptography here ([ADR 0001](adr/0001-thin-client-over-the-server.md)).
The server generates, holds and uses keys; the SDK sends and maps.

## Source layout

| Path | What it holds |
| :--- | :--- |
| `CryptoServiceClient.java` | The public client, its builder, the raw `request`, `close` |
| `CryptoOperations`, `KeyOperations`, `PostQuantumOperations`, `StreamOperations`, `ServiceOperations`, `MetaOperations` | Package-private classes in one inheritance chain, each holding a group of typed operations (split to keep files under 500 lines); `CryptoServiceClient` is the public end |
| `ClientCore.java` | Encode, exchange, decode, the blocking wait and cancellation-preserving `map` |
| `Transport.java`, `Exchange.java`, `Backoff.java`, `Scheduler.java`, `Route.java` | The HTTP plumbing: shared state, one call's attempts, the retry rules, the timer thread, the retry-safety table |
| `Operation.java`, `Decoder.java`, `Json.java` | Typed routes, response decoders (`data` read by JSON pointer from the byte stream), the one Jackson configuration |
| `*Request.java`, `*Result.java`, key-pair and document records, enums | The API's bodies, generated once from the OpenAPI document and maintained by hand |
| `ApiError`, `TransportError`, `CryptoServiceException`, `Problem`, `ValidationError` | The error model ([ADR 0003](adr/0003-error-model.md)) |
| `src/test/java/` | Unit tests against loopback stub servers, the conformance kit runner |
| `src/fuzz/`, `src/bench/` | Jazzer targets with seeds; JMH scenarios |
| `conformance/` | crypto-service's kit, vendored byte for byte |

## A call, step by step

1. A typed method, `hashAsync(request)` say, calls `call(HASH, request)`:
   the record is serialised once to bytes (null members left out).
2. `Transport.exchange` starts an `Exchange`, which builds the request
   for each attempt: `User-Agent: crypto-service-sdk-java/<version>`,
   `Content-Type` when there is a body, `x-api-key` and/or
   `Authorization: Bearer` unless the route is public, a `traceparent`
   when configured, and the per-attempt timeout.
3. The shared `HttpClient` sends it asynchronously. A 429, 503 or 504,
   or a lost connection, may be retried after `Backoff`'s wait (full
   jitter, or `Retry-After` up to `maxDelay`); a route that creates or
   deletes state is retried only after 429 or 503
   ([ADR 0004](adr/0004-async-model.md)). A deadline, when set, fails
   the whole call with a timed-out `TransportError`.
4. `Operation.result` decodes a 2xx into the result record, or raises
   `ApiError` with the parsed problem. A 2xx that does not decode raises
   `TransportError` without quoting the body.
5. The caller's future completes; cancelling it cancels the exchange
   and any scheduled retry.

## Where the guarantees are enforced

| Guarantee | Enforced by |
| :--- | :--- |
| The client agrees with the server | `ConformanceTest`: every kit vector against a live server in CI, through the typed methods where they apply |
| Every typed method reaches its route and decodes | `OperationsTest` (63 routes) and `make examples` |
| Hostile responses never escape the error contract | `ProblemFuzzTest`, `ResponseDecodingFuzzTest` |
| No secrets in `toString` or messages | `ModelTest`, `ErrorModelTest`, `ClientTest` |
| 100 % line and branch coverage | JaCoCo `check` in `mvn verify` |
| Complexity ceilings | PMD (`config/pmd/complexity.xml`) in `mvn verify` |
| No warnings | `javac -Xlint:all -Werror` |
| Pinned supply chain | `.mvn/checksums/`, the enforcer plugin, actions pinned by SHA |
| Versions agree everywhere | `scripts/verify-release-versions` |
