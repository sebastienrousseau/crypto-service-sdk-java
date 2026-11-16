<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

<p align="center">
  <img src="https://cloudcdn.pro/crypto-service/v1/logos/crypto-service.svg" alt="Crypto Service logo" width="128" />
</p>

<h1 align="center">Crypto Service SDK for Java</h1>

<p align="center">
  A thin, typed Java client for the Crypto Service REST API: hashing, MACs, key derivation, AEAD, server-held Ed25519 and post-quantum keys, HPKE and KMS, all performed by crypto-server.
</p>

<p align="center">
  <a href="https://github.com/sebastienrousseau/crypto-service-sdk-java/actions"><img src="https://img.shields.io/github/actions/workflow/status/sebastienrousseau/crypto-service-sdk-java/ci.yml?style=for-the-badge&logo=github" alt="Build" height="28" /></a>
  <a href="https://central.sonatype.com/artifact/co.crypto-service/crypto-service-sdk"><img src="https://img.shields.io/maven-central/v/co.crypto-service/crypto-service-sdk?style=for-the-badge&color=fc8d62&logo=apachemaven" alt="Registry" height="28" /></a>
  <a href="https://sebastienrousseau.github.io/crypto-service-sdk-java/api/"><img src="https://img.shields.io/badge/docs-javadoc-blue?style=for-the-badge&labelColor=555555&logo=readthedocs" alt="Docs" height="28" /></a>
  <a href="https://scorecard.dev/viewer/?uri=github.com/sebastienrousseau/crypto-service-sdk-java"><img src="https://img.shields.io/ossf-scorecard/github.com/sebastienrousseau/crypto-service-sdk-java?style=for-the-badge&label=OpenSSF%20Scorecard&logo=openssf" alt="OpenSSF Scorecard" height="28" /></a>
  <a href="LICENSE-APACHE"><img src="https://img.shields.io/badge/license-Apache--2.0%20OR%20MIT-blue.svg?style=for-the-badge" alt="License: Apache-2.0 OR MIT" height="28" /></a>
  <a href="https://github.com/sebastienrousseau/crypto-service-sdk-java/blob/main/docs/POLICIES.md"><img src="https://img.shields.io/badge/Java-17%2B-93450a.svg?style=for-the-badge&logo=openjdk" alt="Java 17 or newer" height="28" /></a>
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/sebastienrousseau/crypto-service-sdk-java/main/.github/demo.gif" alt="The Hash, SignVerify and MlKem examples calling a local crypto-server: a SHA-256 digest, an Ed25519 signature made with a server-held key and checked, and an ML-KEM-768 shared secret agreed through a server-held key" width="100%" />
</p>

---

## Contents

**Getting started**

- [Install](#install): Maven Central (`co.crypto-service:crypto-service-sdk`), Gradle, or from source
- [Requirements](#requirements): toolchain floor, the server
- [Quick Start](#quick-start): a digest, then a signature with a server-held key

**The Crypto Service ecosystem**

- [The Crypto Service ecosystem](#the-crypto-service-ecosystem): crypto-server, its sibling SDKs and the shared conformance kit

**Library reference**

- [Capabilities at a glance](#capabilities-at-a-glance): the current surface by theme
- [Ecosystem comparison](#ecosystem-comparison): short matrix; full table at [`docs/COMPARISON.md`](docs/COMPARISON.md)
- [Benchmarks](#benchmarks): the scenarios; method at [`docs/BENCHMARKS.md`](docs/BENCHMARKS.md)
- [Features](#features): async and blocking calls, types, errors, retries
- [Configuration](#configuration): base URL, credentials, timeouts, retries, tracing
- [Examples](#examples): runnable example index

**Operational**

- [When not to use Crypto Service](#when-not-to-use-crypto-service): limitations
- [Development](#development): make targets, fuzzing, CI
- [Security](#security): guarantees and reporting
- [Documentation](#documentation): all reference docs
- [Stability guarantees](#stability-guarantees): versioning, the breaking axis, toolchain discipline
- [License](#license)

---

## Install

### As a Java library

```xml
<dependency>
  <groupId>co.crypto-service</groupId>
  <artifactId>crypto-service-sdk</artifactId>
  <version>0.0.40</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
implementation("co.crypto-service:crypto-service-sdk:0.0.40")
```

0.0.40 is the first version, in lockstep with the crypto-service suite;
it is not on Maven Central until its release workflow has run. Each
release is signed with GPG, ships a CycloneDX SBOM and `SHA256SUMS` on
its GitHub release, and carries a build provenance attestation
([`docs/packaging.md`](docs/packaging.md)). The JPMS module name is
`co.cryptoservice.sdk`.

From a clone, build and test it into `target/`:

```sh
make check
```

The one runtime dependency is Jackson databind
(`com.fasterxml.jackson.core:jackson-databind`).

---

## Requirements

- Java 17 or newer; CI builds and tests on Temurin 17 and 21 on Linux
  ([toolchain policy](docs/POLICIES.md)).
- Maven 3.9.0 or newer to build from source (`./mvnw` brings 3.10.0).
- A running crypto-server (`@sebastienrousseau/crypto-server`) to call.
  This SDK is a client: every cryptographic operation happens in the
  server. CI tests against the crypto-service commit pinned in
  [`ci/server.ref`](ci/server.ref).

---

## Quick Start

```java
import co.cryptoservice.sdk.CryptoServiceClient;
import co.cryptoservice.sdk.HashAlgorithm;
import co.cryptoservice.sdk.HashRequest;
import co.cryptoservice.sdk.KeyGenerateResult;
import co.cryptoservice.sdk.KeyIdRequest;
import co.cryptoservice.sdk.SignRequest;
import co.cryptoservice.sdk.SignResult;
import co.cryptoservice.sdk.VerifyRequest;

public final class QuickStart {
  private QuickStart() {}

  public static void main(String[] args) {
    try (CryptoServiceClient client = CryptoServiceClient.builder()
        .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
        .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
        .build()) {
      System.out.println(client.hash(new HashRequest(HashAlgorithm.SHA256, "abc")).digest());

      KeyGenerateResult key = client.generateKeyPair();          // Ed25519, held by the server
      SignResult signed = client.sign(new SignRequest(key.keyId(), "hello"));
      boolean valid = client.verify(
          new VerifyRequest(key.publicKey(), "hello", signed.signature())).valid();
      System.out.println("signature valid: " + valid);
      client.deleteKey(new KeyIdRequest(key.keyId()));
    }
  }
}
```

Start the server first (see [DEVELOPMENT.md](DEVELOPMENT.md)). `hash`
posts to `/v2/hash` and returns the digest as lowercase hex, with
`digestBytes()` for the bytes. `generateKeyPair` keeps the private key
on the server and returns its `keyId`; `sign` takes that `keyId`, so the
private key never reaches the client. A rejection raises `ApiError` with
the server's RFC 9457 problem.

---

## The Crypto Service ecosystem

Crypto Service has one engine, crypto-server, and thin typed clients per
language. Every SDK vendors the same conformance kit from crypto-service
(`conformance/`) and runs every vector through its own client in CI, so
the clients agree on what the server does.

| Component | Purpose | Use case |
| :--- | :--- | :--- |
| [`crypto-service`](https://github.com/sebastienrousseau/crypto-service) | The engine: 18 npm packages, among them crypto-server (the REST API this SDK calls), crypto-lib, the TypeScript SDK, the CLI, KMS adapters and the CBOM tooling | Running the service, or calling it from Node.js |
| **`crypto-service-sdk-java`** | Typed Java client for the REST API | JVM services that need cryptography without holding keys |
| [`crypto-service-sdk-go`](https://github.com/sebastienrousseau/crypto-service-sdk-go), [`-rust`](https://github.com/sebastienrousseau/crypto-service-sdk-rust), [`-python`](https://github.com/sebastienrousseau/crypto-service-sdk-python), [`-dotnet`](https://github.com/sebastienrousseau/crypto-service-sdk-dotnet), [`-cpp`](https://github.com/sebastienrousseau/crypto-service-sdk-cpp) | Typed clients for the same API, proven by the same kit | Services in those languages |
| [crypto-service.co](https://crypto-service.co) | The project site | Documentation and news |

---

## Capabilities at a glance

| Area | Capability | Status |
| :--- | :--- | :--- |
| Probes and metadata | `health()`, `live()`, `ready()`, `serviceInfo()`, `algorithms()` | Supported |
| Compliance | `getCbom()` (CycloneDX 1.6 CBOM), `getDoraCompliance()` (DORA self-assessment) | Supported |
| Symmetric | `encrypt`/`decrypt` (XChaCha20-Poly1305), `secretboxSeal`/`Open`, `passwordEncrypt`/`Decrypt`, `keyWrap`/`keyUnwrap` (AES-KW, AES-KWP) | Supported |
| Hashing and KDF | `hash`, `mac`, `macVerify`, `kdf` (HKDF, PBKDF2, scrypt), `passwordHash`/`Verify` (Argon2id) | Supported |
| Server-held keys | `generateKeyPair`, `exportKey`, `deleteKey`, `sign`, `verify`, `streamSign`, `streamVerify` | Supported |
| Public-key encryption | `sealedboxSeal`/`Open`, `sealedboxSealPq`/`OpenPq`, `multiRecipientEncrypt`, `hpkeGenerateKeyPair`/`Seal`/`Open` | Supported |
| Post-quantum | ML-KEM (`mlKem...`), hybrid X25519 + ML-KEM (`pqGenerateKeyPair`, `pqEncapsulate`, `pqDecapsulate`), ML-DSA (`pqSign...`), SLH-DSA (`pqHashSign...`), PQ streams, ISO 20022 dual signatures | Supported |
| KMS and OPAQUE | `kmsCreateKey`, `kmsWrap`/`Unwrap`, `kmsGenerateDataKey`, `kmsEncrypt`/`Decrypt`; `opaqueRegisterInit`/`Finish`, `opaqueLoginInit`/`Finish` | Supported |
| Raw calls | `request(method, path, body, contentType)`: any route, any status | Supported |
| Deprecated v1 routes, `/metrics` | Not typed (v1 is sunset on 2027-01-01) | Reachable through `request` only |

Every operation has an async form (`xxxAsync`, a `CompletableFuture`)
and a blocking form (`xxx`).

---

## Ecosystem comparison

The six SDKs share one method surface and one resilience contract, and
run crypto-service's conformance kit against a live crypto-server in CI.

| Project | Language (minimum) | Conformance kit in CI | Async model |
| :--- | :---: | :---: | :---: |
| crypto-service `crypto-sdk` | TypeScript (Node.js) | In crypto-service's own tests | Promises |
| **crypto-service-sdk-java** | **Java 17** | **Yes** | **`CompletableFuture`** |
| crypto-service-sdk-go, -rust, -python, -dotnet, -cpp | See each repository | Yes | Native to each language |
| Raw HTTP | Any | No | None |

See [`docs/COMPARISON.md`](docs/COMPARISON.md) for the complete matrix.

---

## Benchmarks

JMH measures four scenarios whose names are the same in every Crypto
Service SDK: `encode_request`, `decode_response` and `decode_problem`
(the client's own cost, no network) and `round_trip_hash` (a blocking
`hash` against crypto-server on loopback, run only when
`CRYPTO_SERVICE_URL` is set). No numbers are published yet;
[`docs/BENCHMARKS.md`](docs/BENCHMARKS.md) describes the method and will
carry generated results.

---

## Features

- **Async first, blocking when you want it.** Each operation returns a
  `CompletableFuture`; its blocking twin waits for it. Cancelling the
  future (`cancel(true)`) aborts the HTTP exchange and any pending retry.
- **One client, thread-safe.** `CryptoServiceClient` holds one pooled
  `java.net.http.HttpClient` (HTTP/2 where offered, keep-alive) and is
  safe to share across threads. Blocking calls park in
  `CompletableFuture.get()`, so they suit virtual threads.
- **Typed requests and results as records**, with the API's encodings
  kept as strings (hex keys and signatures, base64 ciphertexts, UTF-8
  text) and `...Bytes()` accessors that decode exactly the encoding the
  OpenAPI schema names; `Bytes` encodes and decodes for requests.
- **Errors carry the server's problem.** A status outside 2xx raises
  `ApiError` with the RFC 9457 `Problem` (`type`, `title`, `status`,
  `detail`, `instance`, `code`, `errors[]`); a body that is not a
  problem becomes `about:blank`. No usable answer raises
  `TransportError` with `timedOut()`. Both extend
  `CryptoServiceException`.
- **Retries are opt-in and safe.** `RetryOptions` retries 429, 503, 504
  and connection failures with full-jitter backoff and `Retry-After`;
  routes that create or delete server state are retried only after 429
  or 503.
- **No secrets in logs.** Credentials, keys, plaintexts and bodies never
  appear in exception messages or `toString`; the SDK logs nothing.

```java
CryptoServiceClient client = CryptoServiceClient.builder()
    .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
    .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
    .build();

// Async: start several calls, then combine them.
CompletableFuture<HashResult> sha = client.hashAsync(new HashRequest(HashAlgorithm.SHA256, "abc"));
CompletableFuture<HashResult> b3 = client.hashAsync(new HashRequest(HashAlgorithm.BLAKE3, "abc"));
System.out.println(sha.thenCombine(b3, (x, y) -> x.digest() + " " + y.digest()).join());

// Errors: the RFC 9457 problem the server sent.
try {
  client.sign(new SignRequest("k_AAAAAAAAAAAAAAAAAAAAAA", "m"));
} catch (ApiError e) {
  System.out.println(e.status() + " " + e.problem().code());
}
client.close();
```

---

## Configuration

| Builder method | Default | Effect |
| :--- | :--- | :--- |
| `baseUrl(String)` | required | The server, for example `http://127.0.0.1:3101`; http or https with a host, no user info, query or fragment |
| `apiKey(String)` | none | Sent as `x-api-key` |
| `token(String)` | none | A JWT, sent as `Authorization: Bearer <token>` |
| `timeout(Duration)` | 30 seconds | Per attempt: connect and response headers |
| `deadline(Duration)` | none | The whole call, retries and waits included |
| `retry(RetryOptions)` | `RetryOptions.NONE` | `maxRetries` (0), `initialDelay` (200 ms), `maxDelay` (2 s) |
| `traceparent(String)` / `autoTraceparent(boolean)` | none | A fixed W3C `traceparent`, or a fresh one per request |
| `responseListener(ResponseListener)` | none | Sees each response's status and headers (never a body) |
| `httpClient(HttpClient)` | the client's own | Inject a client for tests or a proxy; it must not follow redirects |

The blocking methods work from any thread pool, and from virtual
threads on Java 21 without extra setup:

```java
try (CryptoServiceClient client = CryptoServiceClient.builder()
        .baseUrl(System.getenv().getOrDefault("CRYPTO_SERVICE_URL", "http://127.0.0.1:3101"))
        .apiKey(System.getenv("CRYPTO_SERVICE_API_KEY"))
        .retry(RetryOptions.of(2))
        .deadline(Duration.ofSeconds(10))
        .build()) {
  ExecutorService pool = Executors.newFixedThreadPool(4); // or a virtual-thread executor on 21+
  List<Future<String>> digests = new ArrayList<>();
  for (String text : List.of("a", "b", "c")) {
    digests.add(pool.submit(() -> client.hash(new HashRequest(HashAlgorithm.SHA256, text)).digest()));
  }
  for (Future<String> digest : digests) {
    System.out.println(digest.get());
  }
  pool.shutdown();
}
```

---

## Examples

Each file in [`examples/`](examples/) is a runnable program. `make
examples` compiles them against the built SDK and runs every one against
the server at `CRYPTO_SERVICE_URL`; CI does the same in its conformance
job, so they cannot rot.

| Example | Shows |
| :--- | :--- |
| [`Hash.java`](examples/Hash.java) | `hash(...)`: SHA-256 of "abc", checked against the FIPS 180-2 vector |
| [`AeadRoundTrip.java`](examples/AeadRoundTrip.java) | `encrypt` and `decrypt` (XChaCha20-Poly1305) with a synthetic hex key |
| [`SignVerify.java`](examples/SignVerify.java) | A server-held Ed25519 key: `generateKeyPair`, `sign` by keyId, `verify`, `deleteKey` |
| [`MlKem.java`](examples/MlKem.java) | ML-KEM-768: `mlKemEncapsulate` to the public key, `mlKemDecapsulate` by keyId |
| [`ErrorHandling.java`](examples/ErrorHandling.java) | `ApiError` with the problem's `code` and `errors[]`, and `TransportError` |
| [`ConcurrentHashes.java`](examples/ConcurrentHashes.java) | Twenty calls in flight at once through the async API |

---

## When not to use Crypto Service

- **You cannot reach a crypto-server.** Every call goes to it; nothing
  works offline, and the SDK has no local fallback by design
  ([ADR 0001](docs/adr/0001-thin-client-over-the-server.md)).
- **You need the deprecated v1 routes or `/metrics` as typed methods.**
  v1 is sunset on 2027-01-01 and `/metrics` is for scrapers; both are
  reachable only through `request`.
- **You need very high request rates from one principal.** crypto-server
  meters each principal (60 requests a minute at the pinned commit);
  batch with `streamSign`/`streamVerify` or spread load over principals.
- **You are on Java 11 or older.** The floor is Java 17.

---

## Development

```sh
make check
make regression
make conformance
make bench
make fuzz
make docs-lint
```

`make check` runs everything CI's build job runs, without a server:
`javac -Xlint:all -Werror`, the unit tests and fuzz seeds, JaCoCo at
100 % line and branch coverage, PMD with the suite's complexity
ceilings, and the compile of every example and README snippet.
`make regression` builds the pinned crypto-server, starts it on a
loopback port and adds the conformance kit, every example, every README
snippet and the benchmark smoke run; it is what the CI conformance job
runs. `make conformance` runs the kit against `CRYPTO_SERVICE_URL` and
fails if it is unset. Every gate is in [`DEVELOPMENT.md`](DEVELOPMENT.md).

---

## Security

Report a vulnerability privately through
[GitHub private vulnerability reporting](https://github.com/sebastienrousseau/crypto-service-sdk-java/security/advisories/new),
never in a public issue ([SECURITY.md](SECURITY.md)).

The SDK implements no cryptography: keys are generated, held and used
by the server, and the client sends what you give it to the base URL you
configure. It never follows redirects, so credentials cannot reach
another origin, and it has no option to disable TLS verification. It
writes no files and logs nothing. Use https off loopback.

Responses are untrusted input. Two Jazzer fuzz targets
(`src/fuzz/java/`) feed arbitrary bytes to the problem parser and to
every success decoder; their seeds replay in every build and CI fuzzes
each target for 60 seconds on every pull request. Dependencies are
pinned by SHA-256 (`.mvn/checksums/`) and audited with OSV-Scanner over
the SBOM.

---

## Documentation

[User manual](https://sebastienrousseau.github.io/crypto-service-sdk-java/) ·
[API reference (Javadoc)](https://sebastienrousseau.github.io/crypto-service-sdk-java/api/) ·
[Developer docs](DEVELOPMENT.md) ·
[Ecosystem map](docs/COMPARISON.md)

Also: [Architecture](docs/ARCHITECTURE.md) ·
[Decisions](docs/adr/README.md) ·
[Benchmarks](docs/BENCHMARKS.md) ·
[Packaging](docs/packaging.md) ·
[Roadmap](docs/ROADMAP.md) ·
[Policies](docs/POLICIES.md) ·
[Conformance kit](conformance/README.md) ·
[Changelog](CHANGELOG.md) ·
[Governance](GOVERNANCE.md) ·
[Support](SUPPORT.md)

---

## Stability guarantees

The project is pre-1.0 and versions advance in `0.0.1` steps, in
lockstep with the crypto-service suite. The breaking axis is the public
API of `co.cryptoservice.sdk` (method names and signatures, record
components, exception types and their hierarchy); the changelog calls
out any change to it, and a japicmp check in CI compares each build with
the last release on Maven Central once there is one. The client sends
the request shapes the conformance kit pins; a change to what it sends
or to which exception a given answer raises is a breaking change even
when no signature moves. The deprecated v1 routes are excluded from the
typed surface. The Java floor moves only as described in the
[toolchain policy](docs/POLICIES.md), announced in the changelog.

---

## License

This project is dual-licensed under either:

- Apache License, Version 2.0 ([`LICENSE-APACHE`](LICENSE-APACHE) or <https://www.apache.org/licenses/LICENSE-2.0>)
- MIT License ([`LICENSE-MIT`](LICENSE-MIT) or <https://opensource.org/licenses/MIT>)

at your option. Dependencies keep their own licences.
