<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Getting support

## Read first

- [README.md](README.md): install, quick start, configuration and the
  method list.
- [`examples/`](examples/): runnable programs, each run against a real
  server in CI.
- Server behaviour (what a field means, why a request is refused) is
  documented in [crypto-service](https://github.com/sebastienrousseau/crypto-service)
  and its OpenAPI document.

## Questions and bugs

Open an [issue](https://github.com/sebastienrousseau/crypto-service-sdk-java/issues/new/choose)
with the SDK version, your Java version, the server's version (from
`client.serviceInfo().version()`), a minimal reproducer and the
exception message. Never paste real keys, tokens or plaintexts; the
conformance kit's synthetic values reproduce most problems.

If the server, not the client, gives the wrong answer, report it to
[crypto-service](https://github.com/sebastienrousseau/crypto-service/issues).

## Security

Do not open a public issue. Follow [SECURITY.md](SECURITY.md).

## Supported versions

| Version | Supported | Notes |
| :--- | :--- | :--- |
| 0.0.41 | Yes | Current, in preparation on `main`; tested against the crypto-service commit in `ci/server.ref` |
| 0.0.40 | Yes, until 0.0.41 is released | First version, released 2026-11-16 |

Only the latest `0.0.x` gets fixes while the project is pre-1.0. Support
is best effort by a single maintainer ([GOVERNANCE.md](GOVERNANCE.md)).
