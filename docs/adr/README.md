<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Architecture decision records

Decisions that will be questioned later, with the reason they were
made. Each record is immutable once accepted; a later decision that
changes one supersedes it with a new record.

| ADR | Decision | Status |
| :--- | :--- | :--- |
| [0001](0001-thin-client-over-the-server.md) | A thin client over crypto-server, proven by its conformance kit; no local cryptography | Accepted |
| [0002](0002-http-stack.md) | `java.net.http.HttpClient` and Jackson databind, nothing else at runtime | Accepted |
| [0003](0003-error-model.md) | Unchecked `ApiError` and `TransportError` under one base | Accepted |
| [0004](0004-async-model.md) | `CompletableFuture` first, blocking methods that wait for it | Accepted |
