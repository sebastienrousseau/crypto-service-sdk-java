<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Packaging

## Coordinates

| Item | Value |
| :--- | :--- |
| Maven coordinates | `co.crypto-service:crypto-service-sdk` |
| JPMS module name | `co.cryptoservice.sdk` (`Automatic-Module-Name`) |
| Package | `co.cryptoservice.sdk` |
| Java floor | 17 |
| Runtime dependency | `com.fasterxml.jackson.core:jackson-databind` |

## What a release ships

The release workflow (`.github/workflows/release.yml`) builds, from a
signed annotated tag `v0.0.N` whose message is
`crypto-service-sdk-java v0.0.N`:

- the jar, sources jar, Javadoc jar and POM, reproducibly
  (`project.build.outputTimestamp` and one exact JDK build; see
  [reproducing a release](REPRODUCING.md));
- a CycloneDX SBOM (`crypto-service-sdk-0.0.N.cdx.json`);
- `SHA256SUMS` over all of them, and a build provenance attestation for
  the jars (`actions/attest-build-provenance`);
- GPG signatures of every Maven artifact, uploaded to Maven Central
  through the Central Portal from the `release` environment;
- a GitHub release titled `crypto-service-sdk-java 0.0.N` with the
  Highlights, What's Changed, Checksums and Full Changelog sections.

## Verifying a download

Check a jar against `SHA256SUMS` from the GitHub release with
`sha256sum -c`, and its provenance with
`gh attestation verify <jar> --repo sebastienrousseau/crypto-service-sdk-java`.

Every artifact on Maven Central has a detached GPG signature (`.asc`)
made with the maintainer's release signing key:

| Item | Value |
| :--- | :--- |
| Fingerprint | `278D 6076 E275 022D 05B7  0A3F DED7 6FC6 E4AD C8B4` |
| Type | ed25519, sign and certify; created 2026-11-16, expires 2028-11-15 |
| User ID | `Sebastien Rousseau <sebastienrousseau@users.noreply.github.com>` |
| Published on | [keyserver.ubuntu.com](https://keyserver.ubuntu.com/pks/lookup?op=index&search=0x278D6076E275022D05B70A3FDED76FC6E4ADC8B4) (with its user ID) and [keys.openpgp.org](https://keys.openpgp.org/search?q=278D6076E275022D05B70A3FDED76FC6E4ADC8B4) (without one) |

keys.openpgp.org publishes a user ID only after its owner confirms the
email address, and a GitHub no-reply address cannot receive that
email, so the copy there carries the key alone and GnuPG refuses to
import it ("contains no user ID"). Fetch it from keyserver.ubuntu.com
and check the fingerprint against the table above:

```sh
gpg --keyserver hkps://keyserver.ubuntu.com --recv-keys 278D6076E275022D05B70A3FDED76FC6E4ADC8B4
gpg --verify crypto-service-sdk-0.0.40.jar.asc crypto-service-sdk-0.0.40.jar
```

On 2026-11-18 this reported a good signature on the 0.0.40 jar from
Maven Central. Release tags and commits are signed with SSH keys
instead; [`KEYS.asc`](../KEYS.asc) lists them and says how to verify.

To go further, rebuild the release from its tag and compare every
asset with what was published: [reproducing a release](REPRODUCING.md).
`make sbom` writes the CycloneDX SBOM of a local build to
`target/bom.json`.
