<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Packaging

## Coordinates

| Item | Value |
| :--- | :--- |
| Maven coordinates | `co.cryptoservice:crypto-service-sdk` |
| JPMS module name | `co.cryptoservice.sdk` (`Automatic-Module-Name`) |
| Package | `co.cryptoservice.sdk` |
| Java floor | 17 |
| Runtime dependency | `com.fasterxml.jackson.core:jackson-databind` |

## What a release ships

The release workflow (`.github/workflows/release.yml`) builds, from a
signed annotated tag `v0.0.N` whose message is
`crypto-service-sdk-java v0.0.N`:

- the jar, sources jar, Javadoc jar and POM, reproducibly
  (`project.build.outputTimestamp`);
- a CycloneDX SBOM (`crypto-service-sdk-0.0.N.cdx.json`);
- `SHA256SUMS` over all of them, and a build provenance attestation for
  the jars (`actions/attest-build-provenance`);
- GPG signatures of every Maven artifact, uploaded to Maven Central
  through the Central Portal from the `release` environment;
- a GitHub release titled `crypto-service-sdk-java 0.0.N` with the
  Highlights, What's Changed, Checksums and Full Changelog sections.

## Verifying a download

```sh
make sbom
```

Check a jar against `SHA256SUMS` from the GitHub release with
`sha256sum -c`, and its provenance with
`gh attestation verify <jar> --repo sebastienrousseau/crypto-service-sdk-java`.
The Maven signing key's public half is published by the maintainer with
the first release.
