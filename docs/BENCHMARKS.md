<!-- SPDX-License-Identifier: Apache-2.0 OR MIT -->
<!-- SPDX-FileCopyrightText: 2026 Sebastien Rousseau -->

# Benchmarks

No results are published yet. This page describes the scenarios and how
to run them; a later change generates the results table from
`target/jmh-client.txt` and `target/jmh-loopback.txt`.

## Scenarios

The scenario names are the same in every Crypto Service SDK, so results
can be compared across languages.

| Scenario | Class | What it measures |
| :--- | :--- | :--- |
| `encode_request` | `ClientSideBenchmark` | A `POST /v2/hash` request record serialised to JSON and built into the `HttpRequest` the client sends |
| `decode_response` | `ClientSideBenchmark` | A captured `/v2/hash` 200 body decoded into `HashResult` |
| `decode_problem` | `ClientSideBenchmark` | A captured `validation-failed` body parsed into a `Problem` |
| `round_trip_hash` | `LoopbackBenchmark` | One blocking `hash(...)` against crypto-server on loopback |

## Method

- JMH 1.37, one fork. Client-side scenarios: average time, 3 x 1 s
  warm-up, 5 x 1 s measurement (`BENCH_OPTS`).
- `round_trip_hash` is single-shot time, 5 warm-up and 50 measured calls,
  because crypto-server meters each principal at 60 requests a minute;
  a saturating loop would measure the quota. With
  `CRYPTO_SERVICE_JWT_SECRET` set it runs as a fresh principal.
- Record the CPU, OS, JDK and server commit with any result, and treat a
  difference within the error bars as noise.

## Running

```sh
make bench
make bench-smoke
```

`make bench` writes the two result files; `CRYPTO_SERVICE_URL` (and
optionally `CRYPTO_SERVICE_JWT_SECRET`) enables `round_trip_hash`.
`make bench-smoke` runs every scenario briefly and asserts nothing; CI
runs it to keep the benchmarks compiling and running.
