// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fuzzes the success-response decoder: whatever a 2xx body holds, every result type's decoder
 * either returns a value or fails with an {@link IOException} (which the client turns into a
 * {@link TransportError}); no other exception escapes, and no value it returns is null.
 *
 * <p>The seeds under {@code ResponseDecodingFuzzTestInputs/} run as regression tests in {@code mvn
 * verify}; with {@code JAZZER_FUZZ=1} ({@code make fuzz}) it fuzzes.
 */
class ResponseDecodingFuzzTest {

  private static final List<Decoder<?>> DECODERS = decoders();

  private static List<Decoder<?>> decoders() {
    List<Decoder<?>> out = new ArrayList<>();
    out.add(Decoder.dataString());
    out.add(Decoder.dataMap());
    try {
      for (Class<?> type : Synthetic.bodyRecords()) {
        out.add(Decoder.data(type));
        out.add(Decoder.bare(type));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return out;
  }

  @FuzzTest(maxDuration = "60s")
  void anySuccessBodyDecodesOrFailsCleanly(FuzzedDataProvider data) {
    byte[] body = data.consumeRemainingAsBytes();
    for (Decoder<?> decoder : DECODERS) {
      try {
        assertNotNull(decoder.decode(body));
      } catch (IOException expected) {
        // Malformed or mistyped: the client raises TransportError.malformed.
      }
    }
  }
}
