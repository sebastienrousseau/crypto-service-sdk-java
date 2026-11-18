<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Reproducing a release

Every asset of a release (the jar, the sources and Javadoc jars, the POM
and the CycloneDX SBOM) can be rebuilt from its signed tag, bit for bit,
and checked against what was published: the release's `SHA256SUMS` on
GitHub and the `.sha256` files on Maven Central. This page says what
makes that work, how to do it, and what was verified.

## What makes the build reproducible

| Input | How it is fixed |
| :--- | :--- |
| Source | The signed annotated tag `v0.0.N` (`git verify-tag` with [`KEYS.asc`](../KEYS.asc)) |
| Timestamps | `project.build.outputTimestamp` in `pom.xml`: every jar entry carries that date |
| Maven | 3.10.1 through `./mvnw`, which checks the distribution's SHA-256 |
| Plugins and dependencies | Exact versions (the enforcer refuses ranges and unpinned plugins), each artifact checked against `.mvn/checksums/` |
| JDK | One exact Temurin build: `21.0.13+9` on Linux x64, named in `.github/workflows/release.yml` and `mise.toml` |
| SBOM | The CycloneDX plugin writes no timestamp and derives its serial number (a name-based UUID) from the project, not at random |
| Javadoc | Same-class links carry an explicit label, so the generated index does not depend on the order the file system lists the sources in |

The JDK build matters for the jars. javac's output is the same across
JDK 21 builds, but the jars are deflate-compressed by the JDK's zlib, and
another build may compress the same bytes differently. Rebuilding 0.0.40
with Oracle's OpenJDK 21.0.2 gave class files identical to the published
ones but different jar bytes, and Temurin 17 also writes
`Build-Jdk-Spec: 17` into the manifest. Hence the exact build.

## How to reproduce a release

```sh
git clone https://github.com/sebastienrousseau/crypto-service-sdk-java.git
cd crypto-service-sdk-java
mise install                      # Temurin 21.0.13+9, from mise.toml
make reproduce VERSION=0.0.40
```

`make reproduce` runs `scripts/reproduce-release`, which:

1. verifies the tag's SSH signature against `KEYS.asc`;
2. checks that `java` is the Temurin build the release was made with
   (the one in the tag's release workflow; for 0.0.40, which predates
   that pin, `21.0.13+9`, read from the release run's log), and stops
   otherwise unless `REPRODUCE_ANY_JDK=1`;
3. extracts the tag into a temporary directory and runs
   `./mvnw -Prelease -DskipTests -Dgpg.skip=true package` and
   `./mvnw cyclonedx:makeBom -DoutputFormat=json`, the goals the release
   workflow runs, unsigned;
4. checks the five assets with `sha256sum -c` against the release's
   `SHA256SUMS`, and the jars and POM against Maven Central's `.sha256`
   files; any difference fails it.

Without `mise`, install Temurin 21.0.13+9 any other way and put it first
on `PATH`. The GPG signatures on Maven Central are over these same
bytes, so they verify against a reproduced jar too
([packaging](packaging.md#verifying-a-download)).

### Expected checksums for 0.0.40

From the [`SHA256SUMS`](https://github.com/sebastienrousseau/crypto-service-sdk-java/releases/download/v0.0.40/SHA256SUMS)
of the GitHub release `v0.0.40`:

```text
f433bea6decd93aee34f68c3972a757929bb04a1f0daf69feb435d4e75c455c8  crypto-service-sdk-0.0.40-javadoc.jar
540979557a4b670e8a629c827a9b0b6184a3abd03e64ac84771adb0b9e848677  crypto-service-sdk-0.0.40-sources.jar
f6a635864f80778e30a171fd8c2e310962e09c56d9b6a31012536fef1b15366e  crypto-service-sdk-0.0.40.cdx.json
c4c15d1b23a7b2d07a829e188d00b5e737712d010e7fd3cb5b52aa34dfe86045  crypto-service-sdk-0.0.40.jar
4b38b2aeb55a3e85b780217ae3c1eb934cac9161a165251721952f2434b2bd3f  crypto-service-sdk-0.0.40.pom
```

**One known caveat for 0.0.40.** Its Javadoc jar depends on the order
in which the file system lists the source files: built in a directory
on a disk (ext4 on the CI runner, btrfs locally) it matches; built on a
tmpfs, one line of `index-all.html` differs (`ciphertext()` instead of
`AeadResult.ciphertext()`). If `/tmp` is a tmpfs, run
`TMPDIR=$HOME/tmp make reproduce VERSION=0.0.40` (any directory on a
disk). The labelled links above remove this from later releases, and
CI now checks for it.

## What was verified

On 2026-11-18, on Linux x86_64 with Temurin 21.0.13+9, `make reproduce
VERSION=0.0.40` (building on disk) reproduced all five assets of
`v0.0.40`: each matched the release's `SHA256SUMS`, and the jar, sources
jar, Javadoc jar and POM matched Maven Central's `.sha256`. The release
itself was built on a GitHub-hosted Ubuntu runner, so this rebuild ran
on a different machine and Linux distribution, by the maintainer; no
third party has reproduced a release yet.

## How CI keeps it reproducible

- **Every push and pull request**: the CI job *Reproducible build (two
  directories)* runs `make repro-check`, which builds the release assets
  of the tree twice, once under `target/` and once under `/dev/shm` (a
  tmpfs, which lists files in a different order), and fails unless all
  four are identical. On the sources before the links were labelled it
  fails on the Javadoc jar; with them it passes.
- **Every release**: the publish job rebuilds the jar, sources jar and
  Javadoc jar on a fresh runner before signing and checks them against
  the `SHA256SUMS` the build job wrote.

## Changing the JDK build

A new Temurin release is adopted in its own commit that changes the
exact build in every workflow and in `mise.toml` together; Dependabot
does not track it. The release workflow of each tag records the build
that release needs.
