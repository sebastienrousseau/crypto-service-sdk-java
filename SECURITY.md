<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Security Policy

## Supported versions

While pre-1.0, the latest released `0.0.x` receives security fixes;
older `0.0.x` versions do not.

| Version | Status | Receives security fixes? |
| :--- | :--- | :--- |
| 0.0.40 | Current | Yes |

## Reporting a vulnerability

**Do not open a public issue for security reports.**

Report privately through GitHub Private Vulnerability Reporting:
<https://github.com/sebastienrousseau/crypto-service-sdk-java/security/advisories/new>.
The maintainer is notified automatically.

**Acknowledgement**: within 48 hours. **Triage**: within 7 days.

A vulnerability in the server or its cryptography belongs to
[crypto-service](https://github.com/sebastienrousseau/crypto-service/security/advisories/new).

## Security posture

### Scope

The SDK is an HTTP client. It serialises the records you pass to JSON,
sends them to the base URL you configure and parses the answer. It
implements no cryptography, writes no files and logs nothing.

### Threat model

| Surface | How it's handled |
| :--- | :--- |
| **Secrets in logs** | Exception messages name only the status, problem type, method and path; every record's `toString` redacts keys, plaintexts, passwords, signatures and ciphertexts; the builder's `toString` redacts credentials. |
| **Log forging** | Text the server chose (problem members, validation errors) has its control characters and line separators escaped before it reaches a message or `toString`. |
| **Credential exposure** | Sent only to the configured base URL; redirects are never followed; a base URL with user info is refused; public routes (`/health`, `/live`, `/ready`, OPAQUE login) are sent without credentials. Use https off loopback. |
| **TLS** | The JVM's default trust store and hostname verification; the SDK has no option to disable them. An injected `HttpClient` must not follow redirects; its TLS settings are the caller's. |
| **Hostile server responses** | Parsed by Jackson into records; a malformed 2xx body raises `TransportError` without quoting it; any error body becomes a `Problem`. Both paths are fuzzed. |
| **Duplicate state changes** | Retries are off by default; when on, key generation, deletion, KMS key creation and OPAQUE registration are retried only after 429 or 503. |
| **Dependency CVEs** | One runtime dependency, pinned by version and SHA-256, audited with OSV-Scanner, updated by Dependabot. |

### Assurance case and reviews

[docs/ASSURANCE.md](docs/ASSURANCE.md) is the full argument: the
security requirements, assets, actors, entry points and trust
boundaries, how secure design principles are applied, and how each
relevant CWE Top 25 and OWASP weakness is countered, with the code and
tests behind every claim. The latest security review is
[docs/security-review-2026-11.md](docs/security-review-2026-11.md).

### Cryptography status

The SDK implements no cryptography; TLS is the JDK's `HttpClient`.
