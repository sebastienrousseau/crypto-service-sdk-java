<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->

# Roadmap

Direction, not commitments. Each item lands on the release branch with
its tests, and only when the server and the conformance kit support it.

## Next

- Render `.github/demo.gif` from `.github/demo.tape`. (0.0.40, the first
  release, is on Maven Central; see [packaging](packaging.md).)
- Generate `docs/BENCHMARKS.md` results from `make bench` on a recorded
  machine.
- Follow each crypto-service release: bump `ci/server.ref`, re-vendor
  the kit, regenerate any record whose schema changed.

## Later

- Streaming request bodies for large plaintexts, if crypto-server adds
  streaming routes.
- A Halstead-difficulty check in CI if a maintained Java tool appears
  (it is the one complexity ceiling not measured today).
