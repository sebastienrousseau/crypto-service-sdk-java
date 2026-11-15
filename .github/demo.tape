# SPDX-License-Identifier: Apache-2.0 OR MIT
#
# Rendered by `make demo` (vhs, https://github.com/charmbracelet/vhs) from
# the repository root, against the crypto-server at CRYPTO_SERVICE_URL
# (start one with `make regression`'s steps, or see DEVELOPMENT.md). The
# recording runs three examples compiled by `make demo` beforehand: Hash
# (SHA-256 of "abc"), SignVerify (a server-held Ed25519 key) and MlKem
# (ML-KEM-768 by keyId). Keys and values are synthetic; nothing leaves the
# loopback interface. `make demo` exports DEMO_ROOT and DEMO_CP.
Output .github/demo.gif

Set Shell "bash"
Set FontSize 14
Set Width 1200
Set Height 640
Set Padding 20
Set Theme "Catppuccin Mocha"

Hide
Type `cd "$DEMO_ROOT" && export PS1='$ ' && clear`
Enter
Show

Type "grep -n 'client\.' examples/SignVerify.java"
Sleep 500ms
Enter
Wait@30s /^\$$/
Sleep 3s

Type `java -cp "$DEMO_CP" Hash`
Sleep 500ms
Enter
Wait@60s /^\$$/
Sleep 2s

Type `java -cp "$DEMO_CP" SignVerify`
Sleep 500ms
Enter
Wait@60s /^\$$/
Sleep 3s

Type `java -cp "$DEMO_CP" MlKem`
Sleep 500ms
Enter
Wait@60s /^\$$/
Sleep 5s
