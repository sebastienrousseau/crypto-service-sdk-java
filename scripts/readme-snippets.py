#!/usr/bin/env python3
# SPDX-License-Identifier: Apache-2.0 OR MIT
# SPDX-FileCopyrightText: 2026 Sebastien Rousseau
"""Extracts every fenced code block of README.md and checks it.

  python3 -I scripts/readme-snippets.py                 # compile and run
  python3 -I scripts/readme-snippets.py --compile-only  # no server needed

- ```java: compiled with javac --release 17 -Xlint:all -Werror against
  the built SDK and, unless --compile-only, run against
  CRYPTO_SERVICE_URL / CRYPTO_SERVICE_API_KEY. A block without a class
  declaration is wrapped in a main method with the SDK imported.
- ```xml: must be the <dependency> for this artifact at pom.xml's version.
- ```kotlin: must be the Gradle coordinate at pom.xml's version.
- ```sh: every line is a `make <target>` (checked with `make -n`).

Any other block fails the check, so nothing in the README goes unverified.
"""
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "target", "readme-snippets")
WRAP = """import co.cryptoservice.sdk.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public final class {name} {{
  private {name}() {{}}

  public static void main(String[] args) throws Exception {{
{body}
  }}
}}
"""


def blocks(text):
    for m in re.finditer(r"^```(\w*)\n(.*?)^```$", text, re.M | re.S):
        yield m.group(1), m.group(2), text[: m.start()].count("\n") + 1


def pom_coordinates():
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    root = ET.parse(os.path.join(ROOT, "pom.xml")).getroot()
    return tuple(root.find(f"m:{k}", ns).text for k in ("groupId", "artifactId", "version"))


def check_xml(code, line):
    dep = ET.fromstring(code)
    got = tuple(dep.findtext(k) for k in ("groupId", "artifactId", "version"))
    if dep.tag != "dependency" or got != pom_coordinates():
        sys.exit(f"README.md:{line}: xml block is not the dependency {pom_coordinates()}: {got}")


def check_kotlin(code, line):
    want = 'implementation("%s:%s:%s")' % pom_coordinates()
    if code.strip() != want:
        sys.exit(f"README.md:{line}: kotlin block must be exactly {want}")


def check_sh(code, line):
    for cmd in code.strip().splitlines():
        parts = cmd.split()
        if len(parts) != 2 or parts[0] != "make":
            sys.exit(f"README.md:{line}: sh blocks hold `make <target>` lines only, not {cmd!r}")
        subprocess.run(["make", "-n", parts[1]], cwd=ROOT, check=True, stdout=subprocess.DEVNULL)


def java_source(code, index):
    m = re.search(r"\bclass\s+(\w+)", code)
    if m:
        return m.group(1), code
    name = f"ReadmeSnippet{index}"
    body = "\n".join(("    " + l) if l else "" for l in code.rstrip().splitlines())
    return name, WRAP.format(name=name, body=body)


def main():
    compile_only = "--compile-only" in sys.argv
    with open(os.path.join(ROOT, "README.md"), encoding="utf-8") as f:
        text = f.read()
    checks = {"xml": check_xml, "kotlin": check_kotlin, "sh": check_sh}
    os.makedirs(OUT, exist_ok=True)
    programs = []
    for lang, code, line in blocks(text):
        if lang in checks:
            checks[lang](code, line)
        elif lang == "java":
            name, source = java_source(code, len(programs) + 1)
            with open(os.path.join(OUT, name + ".java"), "w", encoding="utf-8") as f:
                f.write(source)
            programs.append((name, line))
        else:
            sys.exit(f"README.md:{line}: unchecked code block language {lang!r}")
    with open(os.path.join(ROOT, "target", "classpath.txt"), encoding="utf-8") as f:
        cp = os.path.join(ROOT, "target", "classes") + os.pathsep + f.read().strip()
    sources = [os.path.join(OUT, n + ".java") for n, _ in programs]
    subprocess.run(
        ["javac", "--release", "17", "-Xlint:all", "-Werror", "-d", os.path.join(OUT, "classes"), "-cp", cp]
        + sources,
        check=True,
    )
    print(f"readme-snippets: {len(programs)} java blocks compiled")
    if compile_only:
        return
    for name, line in programs:
        print(f"== README.md:{line} ({name})", flush=True)
        subprocess.run(["java", "-cp", os.path.join(OUT, "classes") + os.pathsep + cp, name], check=True)
    print(f"readme-snippets: {len(programs)} java blocks ran")


main()
