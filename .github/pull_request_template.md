<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

## What this changes

<!-- What behaviour changes, and why. -->

## How it was verified

<!-- What you ran, and what you observed. Name the test and the result it
     produced rather than "tests pass". -->

## Checklist

- [ ] Every commit is signed off (`git commit -s`), see [DCO.txt](../DCO.txt)
- [ ] Tests cover the change, in the same commit; `make check` passes
- [ ] Documentation and CHANGELOG updated if behaviour or interfaces changed
- [ ] Synthetic data only: no real keys, tokens or plaintexts
- [ ] A bug fix has a test that failed without the fix
- [ ] No cryptography in `src/main`; nothing secret in a message or `toString`
- [ ] `docs/ASSURANCE.md` updated if a trust boundary, entry point or release step changed

Reviewers: the checklist is in [CONTRIBUTING.md](../CONTRIBUTING.md#code-review).
