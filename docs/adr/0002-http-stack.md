<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# ADR 0002: java.net.http and Jackson, nothing else at runtime

- Status: Accepted
- Date: 2026-11-15

## Context

The SDK needs an HTTP client with connection pooling, HTTP/2, timeouts,
cancellation and an async API, and a JSON library that maps records.
Candidates were OkHttp, Apache HttpClient 5, Netty-based clients and the
JDK's `java.net.http.HttpClient`.

## Decision

Use the JDK's `HttpClient` (Java 11+, in the floor of 17) and Jackson
databind, as pain001-sdk-java does. One `HttpClient` per
`CryptoServiceClient`, built with HTTP/2 preferred, redirects never
followed and the per-attempt timeout as connect timeout. Jackson stays
behind the package-private `Json` and `Decoder` classes; `JsonNode`
appears in the public API only where the API itself is open-ended (the
CBOM metadata, the ISO 20022 payload, problem extensions).

## Consequences

- One runtime dependency, so a small supply-chain surface and no version
  conflicts on the HTTP stack.
- `sendAsync` gives a `CompletableFuture` whose cancellation aborts the
  exchange, which is the async model of ADR 0004.
- TLS is the JVM's default `SSLContext` with hostname verification; the
  SDK offers no way to turn it off. A caller who injects an `HttpClient`
  owns its TLS settings, and the builder refuses one that follows
  redirects.
- `HttpClient` is `AutoCloseable` only from Java 21; `close()` uses it
  when available and otherwise lets the client be collected.
