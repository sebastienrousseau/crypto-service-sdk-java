<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Policies

## Toolchain

- The language floor is **Java 17** (`maven.compiler.release` 17). CI
  builds and tests on Temurin 17 and 21; `mise.toml` pins JDK 21 for
  local work.
- The floor moves only in a release whose changelog says so; the
  maintainer decides when.
- `./mvnw` runs Maven 3.10.1, checked against its SHA-256. Any Maven
  3.9.0 or newer also builds the project. Every plugin is pinned in
  `pom.xml`, and the enforcer fails the build on an unpinned plugin or a
  dynamic dependency version.

## Versioning

- Versions are `0.0.x` and move by `0.0.1`, in lockstep with the
  crypto-service suite. `pom.xml` is the single source of truth;
  `scripts/verify-release-versions` checks every other file.
- The server contract is the crypto-service commit in `ci/server.ref`;
  `conformance/` is that commit's kit, byte for byte.

## Dependencies

- One runtime dependency: Jackson databind, pinned to an exact version.
  JUnit Jupiter and Jazzer are test-only, JMH is in the `bench` profile
  only, and JaCoCo, PMD, japicmp, fmt-maven-plugin (google-java-format)
  and the release plugins are build plugins. Dependabot proposes
  updates weekly.
- Maven verifies every resolved artifact against the SHA-256 recorded in
  `.mvn/checksums/` and fails on one that is missing or different.
  `make lock` re-records the file.
- `make audit` scans the CycloneDX SBOM with OSV-Scanner.
