// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * The vendored kit is byte for byte the one {@code conformance/SHA256SUMS} pins, and {@code
 * ci/server.ref} names the crypto-service commit it came from.
 */
class KitChecksumTest {

  @Test
  void everyKitFileMatchesItsPinnedHash() throws IOException, NoSuchAlgorithmException {
    Path dir = ConformanceKit.DIR;
    List<String> pinned = new ArrayList<>();
    for (String line : Files.readAllLines(dir.resolve("SHA256SUMS"), StandardCharsets.UTF_8)) {
      String[] parts = line.split("\\s+\\*?", 2);
      pinned.add(parts[1] + " " + parts[0]);
    }
    List<String> actual = new ArrayList<>();
    try (Stream<Path> files = Files.list(dir)) {
      for (Path file :
          files.filter(f -> !f.getFileName().toString().equals("SHA256SUMS")).toList()) {
        actual.add(file.getFileName() + " " + sha256(file));
      }
    }
    pinned.sort(null);
    actual.sort(null);
    assertEquals(pinned, actual);
  }

  @Test
  void serverRefIsAFullCommitSha() throws IOException {
    String ref = Files.readString(Path.of("ci/server.ref"), StandardCharsets.UTF_8).strip();
    assertEquals(40, ref.length());
    assertEquals(ref, ref.replaceAll("[^0-9a-f]", ""));
  }

  private static String sha256(Path file) throws IOException, NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return HexFormat.of().formatHex(digest.digest(Files.readAllBytes(file)));
  }
}
