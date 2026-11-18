<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Crypto Service SDK for Java

A thin, typed Java client for the Crypto Service REST API. The SDK sends
requests to crypto-server and maps the answers to Java records; the
server performs every cryptographic operation and holds every private
key. The shared conformance kit, run against a live server in CI, checks
that this client and its sibling SDKs agree with it.

```xml
<dependency>
  <groupId>co.crypto-service</groupId>
  <artifactId>crypto-service-sdk</artifactId>
  <version>0.0.41</version>
</dependency>
```

The snippet names 0.0.41, the version `main` prepares; until it is
published, use 0.0.40 from Maven Central.

## Where to go next

- [Overview](readme.md): install, quick start, every method and option.
- [API reference](https://sebastienrousseau.github.io/crypto-service-sdk-java/api/):
  the Javadoc for `co.cryptoservice.sdk`.
- [Architecture](ARCHITECTURE.md): how a call flows and where each
  guarantee is enforced.
- [Benchmarks](BENCHMARKS.md): the scenarios and how they are measured.
- [Development](development.md): every CI gate, reproduced locally.
