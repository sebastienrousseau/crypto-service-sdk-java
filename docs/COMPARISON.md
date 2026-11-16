<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Ecosystem comparison

Ways to call crypto-server from an application, and what each one
guarantees. The SDK rows describe the shared contract every Crypto
Service SDK implements; see each repository for its exact toolchain.

| Property | crypto-service `crypto-sdk` (TypeScript) | crypto-service-sdk-java | Other Crypto Service SDKs (Go, Rust, Python, .NET, C++) | Raw HTTP |
| :--- | :--- | :--- | :--- | :--- |
| Typed v2 operations | Most v2 routes | All 59 v2 operations plus `/`, `/health`, `/live`, `/ready` | All v2 operations | None |
| Conformance kit against a live server in CI | In crypto-service's own tests | Yes, every vector | Yes | No |
| Async model | Promises | `CompletableFuture`, blocking twin | Native per language | Yours |
| Retry-safety table (stateful routes) | Yes (`request.ts`) | Same table | Same table | Yours |
| RFC 9457 problems as typed errors | `CryptoApiError` | `ApiError`, `TransportError` | Same model | Yours |
| Redirects refused, TLS always verified | Redirects refused | Yes | Yes | Yours |
| Local cryptography | None | None | None | None |

The v1 routes (deprecated, sunset 2027-01-01) and `/metrics` are not
typed in any of the language SDKs; this one reaches them through
`request`.
