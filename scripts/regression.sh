#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0 OR MIT
#
# The full regression run, the same steps as CI's conformance job: every
# local gate (make check), the conformance kit (every vector, none
# skipped), every example, every README snippet and the benchmark smoke
# run, against a crypto-server this script starts on a loopback port and
# always stops.
#
#   make regression                                   # builds the pinned server
#   CRYPTO_SERVER_DIR=/path/to/crypto-service make regression
#
# Without CRYPTO_SERVER_DIR it clones sebastienrousseau/crypto-service at
# the commit in ci/server.ref into a temporary directory and builds it
# (Node 24, pnpm 11.9.0, `pnpm install --frozen-lockfile`). The API key
# and JWT secret are random per run. Nothing is left behind: the clone,
# the server's working directory and the server process go when the
# script exits, whether it passes or fails. Safe to run again.
set -euo pipefail

cd "$(dirname "$0")/.."
MAKE=${MAKE:-make}
work=$(mktemp -d "${TMPDIR:-/tmp}/crypto-service-regression.XXXXXX")
server_pid=""

cleanup() {
  if [[ -n "$server_pid" ]] && kill -0 "$server_pid" 2> /dev/null; then
    kill "$server_pid" 2> /dev/null || true
    wait "$server_pid" 2> /dev/null || true
  fi
  rm -r -f -- "$work"
}
trap cleanup EXIT INT TERM

step() { printf '\n==> %s\n' "$*"; }

build_server() {
  local ref
  ref=$(tr -d '[:space:]' < ci/server.ref)
  step "building crypto-server at $ref"
  git init -q "$work/server"
  git -C "$work/server" fetch -q --depth 1 https://github.com/sebastienrousseau/crypto-service.git "$ref"
  git -C "$work/server" checkout -q FETCH_HEAD
  (cd "$work/server" && pnpm install --frozen-lockfile && pnpm -r run build)
  CRYPTO_SERVER_DIR="$work/server"
}

random_hex() {
  python3 -I -c 'import secrets; print(secrets.token_hex(24))'
}

free_port() {
  python3 -I -c 'import socket; s = socket.socket(); s.bind(("127.0.0.1", 0)); print(s.getsockname()[1]); s.close()'
}

start_server() {
  local port url
  port=${CRYPTO_SERVICE_PORT:-$(free_port)}
  url="http://127.0.0.1:$port"
  export CRYPTO_SERVICE_API_KEY CRYPTO_SERVICE_JWT_SECRET
  CRYPTO_SERVICE_API_KEY=$(random_hex)
  CRYPTO_SERVICE_JWT_SECRET=$(random_hex)
  step "starting crypto-server on $url"
  (cd "$work" && PORT="$port" HOST=127.0.0.1 NODE_ENV=development RATE_LIMIT_MAX=100000 \
    CRYPTO_API_KEY="$CRYPTO_SERVICE_API_KEY" JWT_SECRET="$CRYPTO_SERVICE_JWT_SECRET" \
    exec node "$CRYPTO_SERVER_DIR/packages/crypto-server/dist/index.js") > "$work/server.log" 2>&1 &
  server_pid=$!
  for _ in $(seq 1 60); do
    if curl -fsS "$url/health" > /dev/null 2>&1; then
      export CRYPTO_SERVICE_URL="$url"
      return 0
    fi
    if ! kill -0 "$server_pid" 2> /dev/null; then break; fi
    sleep 1
  done
  cat "$work/server.log" >&2
  echo "regression: the server did not become healthy" >&2
  return 1
}

if [[ -z "${CRYPTO_SERVER_DIR:-}" ]]; then
  build_server
fi
[[ -f "$CRYPTO_SERVER_DIR/packages/crypto-server/dist/index.js" ]] ||
  { echo "regression: no built server in $CRYPTO_SERVER_DIR" >&2; exit 1; }
start_server

step "every local gate (make check)"
"$MAKE" check

step "conformance kit (every vector, none skipped)"
"$MAKE" conformance
report=target/surefire-reports/TEST-co.cryptoservice.sdk.ConformanceTest.xml
vectors=$(python3 -I -c 'import glob, json; print(sum(len(json.load(open(f))["vectors"]) for f in glob.glob("conformance/*.json")))')
grep -Eq "tests=\"$vectors\"" "$report"
grep -Eq 'skipped="0"' "$report"
grep -Eq 'failures="0"' "$report"
echo "$vectors conformance vectors ran and passed, none skipped"

step "every example"
"$MAKE" examples

step "every README snippet"
"$MAKE" readme-snippets

step "benchmark smoke run (client-side and round_trip_hash)"
"$MAKE" bench-smoke

step "regression passed against $CRYPTO_SERVICE_URL"
