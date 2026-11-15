# SPDX-License-Identifier: Apache-2.0 OR MIT
#
# `make check` runs every local gate CI's build job runs: the version
# check, whitespace format, file size, the Maven build (javac -Xlint:all
# -Werror, unit tests, JaCoCo at 100 % line and branch, PMD complexity
# ceilings) and the examples' compile. The Jazzer fuzz tests run their
# committed seeds as regression tests there too; `make fuzz` fuzzes them.
# The conformance kit runs only through `make conformance`, which fails
# when CRYPTO_SERVICE_URL, CRYPTO_SERVICE_API_KEY or
# CRYPTO_SERVICE_JWT_SECRET is unset.
# `make regression` builds the pinned crypto-server and runs all of it,
# as CI's conformance job does. DEVELOPMENT.md maps every CI job to its
# target.
#
# ./mvnw pins Maven 3.10.0 (SHA-256 checked) and .mvn/maven.config makes
# Maven refuse any artifact whose SHA-256 is not recorded in
# .mvn/checksums/. After a dependency or plugin change, `make lock`
# re-records them from a clean local repository; review the diff.

MVN ?= ./mvnw
OSV_SCANNER ?= osv-scanner
VHS ?= vhs
LOCK_REPO ?= /tmp/crypto-service-sdk-java-lock-repo
TC := -Daether.artifactResolver.postProcessor.trustedChecksums
JAVA_FILES := $(shell find src examples -name '*.java' | LC_ALL=C sort)
EXAMPLE_CP = target/classes:$$(cat target/classpath.txt)
EXAMPLES := $(sort $(basename $(notdir $(wildcard examples/*.java))))
LOCK_PROFILES := bench,release
CENTRAL := https://repo1.maven.org/maven2/co/cryptoservice/crypto-service-sdk
DOCS_PY ?= python3
FUZZ_TARGETS ?= ProblemFuzzTest ResponseDecodingFuzzTest
FUZZ_TIME ?= 60s

.PHONY: help check format-check size verify build test lint conformance classpath example-compile examples readme-snippets readme-compile bench-build bench bench-smoke demo fuzz lock sbom audit clean \
  versions regression api-check javadoc manual docs-lint release-dry-run

help: ## List the targets
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | sed 's/:.*## /: /'

check: versions format-check size verify example-compile readme-compile ## Every local gate

versions: ## Every file states the version in pom.xml
	@scripts/verify-release-versions

regression: ## Gate, conformance kit, examples, README snippets and benchmark smoke against a fresh server
	scripts/regression.sh

api-check: ## japicmp against the latest release on Maven Central (no-op before the first)
	@latest=$$(curl -fsS "$(CENTRAL)/maven-metadata.xml" 2>/dev/null | sed -n 's:.*<release>\(.*\)</release>.*:\1:p'); \
	if [ -z "$$latest" ]; then \
	  echo "api-check: no release of co.cryptoservice:crypto-service-sdk on Maven Central yet; nothing to compare"; exit 0; fi; \
	mkdir -p target; jar="target/japicmp-baseline.jar"; \
	curl -fsSL -o "$$jar" "$(CENTRAL)/$$latest/crypto-service-sdk-$$latest.jar"; \
	want=$$(curl -fsSL "$(CENTRAL)/$$latest/crypto-service-sdk-$$latest.jar.sha256" | cut -c1-64); \
	got=$$(shasum -a 256 "$$jar" 2>/dev/null || sha256sum "$$jar"); \
	[ "$${got%% *}" = "$$want" ] || { echo "api-check: baseline $$latest does not match its .sha256"; exit 1; }; \
	echo "api-check: comparing with $$latest"; \
	$(MVN) -B -q -DskipTests package japicmp:cmp -Djapicmp.baseline.jar="$(CURDIR)/$$jar"

javadoc: ## The Javadoc (doclint all, warnings fail) into target/reports/apidocs
	$(MVN) -B -q -DskipTests compile javadoc:javadoc

manual: javadoc ## The user manual (MkDocs strict) with the Javadoc under site/api/
	$(DOCS_PY) -m mkdocs build --strict
	mkdir -p site/api && cp -R target/reports/apidocs/. site/api/

docs-lint: ## markdownlint, codespell, REUSE and lychee (offline) over the docs
	npx --yes markdownlint-cli2@0.23.3 "**/*.md"
	codespell
	reuse lint
	lychee --offline --no-progress --include-fragments --exclude-path target --exclude-path site --exclude-path conformance '**/*.md'

release-dry-run: ## The release build (jars, sources, Javadoc, SBOM, bundle), signed with GPG_KEYNAME if set, not published
	$(MVN) -B -Prelease deploy cyclonedx:makeBom -DoutputFormat=json \
	  -Dcentral.skipUpload=true -Dcentral.ignorePublished=false -s ci/dry-run-settings.xml $(if $(GPG_KEYNAME),-Dgpg.keyname=$(GPG_KEYNAME),-Dgpg.skip=true)

format-check: ## No tabs, no trailing spaces, LF endings, final newline; library and examples within 100 columns
	@bad=0; \
	for f in $(filter-out src/test/%,$(JAVA_FILES)); do \
	  if awk 'length > 100 {print FILENAME ":" FNR ": over 100 columns"; found = 1} END {exit !found}' "$$f"; then bad=1; fi; \
	done; \
	for f in $(JAVA_FILES) pom.xml config/pmd/complexity.xml; do \
	  if grep -n "$$(printf '\t')" "$$f"; then echo "tab in $$f"; bad=1; fi; \
	  if grep -nE ' +$$' "$$f"; then echo "trailing space in $$f"; bad=1; fi; \
	  if grep -q "$$(printf '\r')" "$$f"; then echo "CRLF in $$f"; bad=1; fi; \
	  if [ -n "$$(tail -c1 "$$f")" ]; then echo "no final newline in $$f"; bad=1; fi; \
	done; exit $$bad

size: ## Every Java file is at most 500 lines
	@bad=0; \
	for f in $(JAVA_FILES); do \
	  n=$$(wc -l < "$$f"); \
	  if [ "$$n" -gt 500 ]; then echo "$$f: $$n lines (limit 500)"; bad=1; fi; \
	done; exit $$bad

verify: ## Build, unit tests, fuzz seeds, JaCoCo 100 % line and branch, PMD complexity
	$(MVN) -B verify

build: ## Compile only
	$(MVN) -B -DskipTests compile

test: ## Unit tests (the conformance kit skips without CRYPTO_SERVICE_URL)
	$(MVN) -B test

lint: ## javac warnings as errors and the PMD complexity ceilings
	$(MVN) -B -DskipTests compile pmd:check

conformance: ## Every kit vector against CRYPTO_SERVICE_URL; fails when a server variable is unset
	@test -n "$$CRYPTO_SERVICE_URL" || { echo "conformance: set CRYPTO_SERVICE_URL to a running crypto-server"; exit 1; }
	@test -n "$$CRYPTO_SERVICE_API_KEY" || { echo "conformance: set CRYPTO_SERVICE_API_KEY to its CRYPTO_API_KEY"; exit 1; }
	@test -n "$$CRYPTO_SERVICE_JWT_SECRET" || { echo "conformance: set CRYPTO_SERVICE_JWT_SECRET to its JWT_SECRET (scoped vectors, one principal per file)"; exit 1; }
	$(MVN) -B test -Dtest='ConformanceTest,KitChecksumTest' -Dsurefire.failIfNoSpecifiedTests=false \
	  -Dconformance.required=true -Djacoco.skip=true

classpath:
	$(MVN) -B -q dependency:build-classpath -Dmdep.outputFile=target/classpath.txt -Dmdep.includeScope=runtime

example-compile: classpath ## Compile examples/ against the built SDK
	javac --release 17 -Xlint:all -Werror -d target/example-classes -cp "$(EXAMPLE_CP)" examples/*.java

examples: example-compile ## Run every example against CRYPTO_SERVICE_URL; fails on the first failure
	@test -n "$$CRYPTO_SERVICE_URL" || { echo "examples: set CRYPTO_SERVICE_URL to a running crypto-server"; exit 1; }
	@for e in $(EXAMPLES); do \
	  echo "== $$e"; \
	  java -cp "target/example-classes:$(EXAMPLE_CP)" "$$e" || { echo "example $$e failed"; exit 1; }; \
	done

readme-snippets: classpath ## Check every README.md code block; compile and run its Java against CRYPTO_SERVICE_URL
	@test -n "$$CRYPTO_SERVICE_URL" || { echo "readme-snippets: set CRYPTO_SERVICE_URL to a running crypto-server"; exit 1; }
	python3 -I scripts/readme-snippets.py

readme-compile: classpath ## Check every README.md code block and compile its Java (no server needed)
	python3 -I scripts/readme-snippets.py --compile-only

BENCH_CP = target/test-classes:target/classes:$$(cat target/bench-classpath.txt)
BENCH_OPTS ?= -f 1 -wi 3 -w 1s -i 5 -r 1s
BENCH_SMOKE_OPTS := -f 1 -wi 0 -i 1 -r 100ms
# round_trip_hash takes single timed calls (its annotations: 5 warm-up,
# 50 measured), under crypto-server's 60-a-minute tenant quota.
LOOPBACK_OPTS ?= -f 1
LOOPBACK_SMOKE_OPTS := -f 1 -wi 1 -i 3

bench-build: ## Compile the JMH benchmarks (src/bench, profile bench)
	$(MVN) -B -q -Pbench -DskipTests test-compile dependency:build-classpath \
	  -Dmdep.outputFile=target/bench-classpath.txt -Dmdep.includeScope=test

# round_trip_hash needs CRYPTO_SERVICE_URL; without it only the
# client-side scenarios run, with a message.
bench-run = \
	java -cp "$(BENCH_CP)" org.openjdk.jmh.Main ClientSideBenchmark $(1) && \
	if [ -n "$$CRYPTO_SERVICE_URL" ]; then java -cp "$(BENCH_CP)" org.openjdk.jmh.Main LoopbackBenchmark $(2); \
	else echo "CRYPTO_SERVICE_URL unset: skipping round_trip_hash (client-side only)"; fi

bench: bench-build ## Every benchmark, full run; results in target/jmh-client.txt and target/jmh-loopback.txt
	@$(call bench-run,$(BENCH_OPTS) -rf text -rff target/jmh-client.txt,$(LOOPBACK_OPTS) -rf text -rff target/jmh-loopback.txt)

bench-smoke: bench-build ## Every benchmark briefly: kept compiling and running, not asserted
	@$(call bench-run,$(BENCH_SMOKE_OPTS),$(LOOPBACK_SMOKE_OPTS))

fuzz: ## Fuzz each Jazzer target for FUZZ_TIME (60s); findings land in its seed directory
	@for t in $(FUZZ_TARGETS); do \
	  JAZZER_FUZZ=1 JAZZER_MAX_DURATION=$(FUZZ_TIME) $(MVN) -B test -Dtest="$$t" -Djacoco.skip=true || exit 1; \
	done

demo: build example-compile ## Render .github/demo.gif with vhs against CRYPTO_SERVICE_URL (see CONTRIBUTING)
	@command -v $(VHS) > /dev/null || { echo "demo: vhs not found (https://github.com/charmbracelet/vhs)"; exit 1; }
	@test -n "$$CRYPTO_SERVICE_URL" || { echo "demo: set CRYPTO_SERVICE_URL to a running crypto-server"; exit 1; }
	DEMO_ROOT="$(CURDIR)" DEMO_CP="$(CURDIR)/target/example-classes:$(CURDIR)/target/classes:$$(cat target/classpath.txt)" \
	  $(VHS) .github/demo.tape

lock: ## Re-record .mvn/checksums from a clean local repository
	rm -rf .mvn/checksums $(LOCK_REPO)
	$(MVN) -B -q -Dmaven.repo.local=$(LOCK_REPO) $(TC).record=true -P$(LOCK_PROFILES) \
	  clean deploy pmd:check dependency:build-classpath cyclonedx:makeBom javadoc:javadoc japicmp:cmp \
	  -Dmdep.outputFile=target/classpath.txt -DoutputFormat=json -DincludeTestScope=true \
	  -Dgpg.skip=true -Dcentral.skipUpload=true -Dcentral.ignorePublished=false -Djapicmp.skip=true -s ci/dry-run-settings.xml
	rm -rf $(LOCK_REPO)

sbom: ## CycloneDX SBOM of the resolved dependencies (target/bom.json)
	$(MVN) -B -q cyclonedx:makeBom -DoutputFormat=json -DincludeTestScope=true

audit: sbom ## OSV-Scanner over the SBOM; fails on any known advisory
	@$(OSV_SCANNER) scan source -L target/bom.json > target/osv.txt 2>&1; rc=$$?; \
	cat target/osv.txt; \
	grep -Eq 'found [1-9][0-9]* packages' target/osv.txt || { echo "audit: no packages scanned"; exit 1; }; \
	exit $$rc

clean: ## Delete the Maven target directory
	$(MVN) -B -q clean
