<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Governance

How `crypto-service-sdk-java` is run, who decides, and what happens if
its maintainer is unavailable.

## Scope

A typed Java client for the Crypto Service REST API. Cryptography belongs
to [`crypto-service`](https://github.com/sebastienrousseau/crypto-service)
and is governed there; this repository decides only how Java callers
reach it ([ADR 0001](docs/adr/0001-thin-client-over-the-server.md)).

## Roles and responsibilities

| Role | Who | Responsible for |
| :--- | :--- | :--- |
| **Maintainer** | Sebastien Rousseau ([@sebastienrousseau](https://github.com/sebastienrousseau)), the only maintainer | Merging pull requests, cutting and signing releases, Maven Central publication, security response, keeping the SDK in lockstep with the suite |
| **Contributor** | Anyone with a merged pull request | Proposing changes with tests, reviewing others' changes |
| **User** | Everyone | Reporting bugs, asking questions, requesting features ([SUPPORT.md](SUPPORT.md)) |

## Decisions

- **Day-to-day changes** land on the release branch
  `feat/v<next-version>` and reach `main` through its one pull request,
  once CI is green.
- **Significant changes** (a breaking API change, a new runtime
  dependency, a Java floor raise, a change to the error or async model)
  start as an issue and are recorded as an ADR in `docs/adr/`.
- **The version** moves by `0.0.1`, in lockstep with the suite; the
  maintainer decides when a release opens.

CI is the definition of done ([DEVELOPMENT.md](DEVELOPMENT.md)).

## Continuity plan

Items marked **Not yet arranged** have no continuity arrangement today.

| Asset | Today | Continuity |
| :--- | :--- | :--- |
| Source code | Apache-2.0 OR MIT, public history | Anyone can fork and continue it. |
| GitHub repository | Owned by the maintainer's account | **Not yet arranged:** no second owner. |
| Maven Central namespace `co.crypto-service` | Held by the maintainer | **Not yet arranged:** no second account has publish rights. |
| Central Portal token and GPG signing key | `release` environment secrets set by the maintainer | **Not yet arranged:** no escrow; a successor would publish under a new key in a reviewed change. |
| Release process | `DEVELOPMENT.md` and `.github/workflows/release.yml` (with a dry run) | Anyone with repository and namespace rights can release from the documented process. |

## Conduct and security

Participation follows [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
Vulnerabilities go through [SECURITY.md](SECURITY.md), never a public
issue.
