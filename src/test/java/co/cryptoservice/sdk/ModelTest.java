// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Every request and result record: it round-trips through JSON under its wire names, its byte
 * accessors decode, its factories and withers build, and its {@code toString} hides secrets.
 */
class ModelTest {

  /** Members whose values are secret, or are request or response content. */
  private static final Set<String> SECRET =
      Set.of(
          "key",
          "kek",
          "keyToWrap",
          "plaintext",
          "password",
          "privateKey",
          "secretKey",
          "x25519PrivateKey",
          "mlKemSecretKey",
          "derivedKey",
          "sharedSecret",
          "unwrappedKey",
          "sessionKey",
          "psk",
          "hash",
          "salt",
          "data",
          "message",
          "signature",
          "ciphertext",
          "sealed",
          "encrypted",
          "wrapped",
          "wrappedKey",
          "mac",
          "phc",
          "digest");

  @Test
  void everyRecordRoundTripsUnderItsWireNames() throws Exception {
    List<Class<?>> records = Synthetic.bodyRecords();
    assertTrue(records.size() > 100, "found " + records.size());
    for (Class<?> type : records) {
      Object value = Synthetic.record(type);
      JsonNode json = Json.MAPPER.valueToTree(value);
      for (RecordComponent c : type.getRecordComponents()) {
        String wire = c.getName().equals("packageName") ? "package" : c.getName();
        assertTrue(json.has(wire), type.getSimpleName() + "." + wire);
      }
      assertEquals(value, Json.convert(json, type), type.getSimpleName());
    }
  }

  @Test
  void toStringHidesSecretsAndNamesEveryMember() throws Exception {
    for (Class<?> type : Synthetic.publicRecords()) {
      String text = Synthetic.record(type).toString();
      assertTrue(text.startsWith(type.getSimpleName() + "["), text);
      for (RecordComponent c : type.getRecordComponents()) {
        assertTrue(text.contains(c.getName() + "="), type.getSimpleName() + ": " + c.getName());
        if (SECRET.contains(c.getName()) && type != ValidationError.class) {
          assertTrue(
              text.contains(c.getName() + "=<redacted>"), type.getSimpleName() + ": " + text);
        }
      }
    }
  }

  @Test
  void byteAccessorsDecodeTheDocumentedEncoding() throws Exception {
    int accessors = 0;
    for (Class<?> type : Synthetic.publicRecords()) {
      Object value = Synthetic.record(type);
      for (Method m : type.getDeclaredMethods()) {
        if (m.getName().endsWith("Bytes") && m.getParameterCount() == 0) {
          byte[] bytes = (byte[]) m.invoke(value);
          assertNotNull(bytes, type.getSimpleName() + "." + m.getName());
          accessors++;
        }
      }
    }
    assertTrue(accessors > 50, "found " + accessors);
    HashResult hex = new HashResult("sha256", "abcd", 2);
    assertArrayEquals(new byte[] {(byte) 0xab, (byte) 0xcd}, hex.digestBytes());
    AeadResult base64 = new AeadResult("xchacha20-poly1305", "AAEC");
    assertArrayEquals(new byte[] {0, 1, 2}, base64.ciphertextBytes());
    assertNull(
        new KeyExportResult("a", "k", null, null, null, null, null, null, null).privateKeyBytes());
  }

  @Test
  void requestFactoriesAndWithersBuild() throws Exception {
    int withers = 0;
    for (Class<?> type : Synthetic.publicRecords()) {
      Object full = Synthetic.record(type);
      for (Method m : type.getDeclaredMethods()) {
        if (m.getName().startsWith("with") && m.getParameterCount() == 1) {
          Object copy = m.invoke(full, (Object) null);
          assertEquals(type, copy.getClass());
          withers++;
        }
        if (m.getName().equals("of") && Modifier.isStatic(m.getModifiers())) {
          Object[] args = new Object[m.getParameterCount()];
          for (int i = 0; i < args.length; i++) {
            args[i] = Synthetic.value(m.getGenericParameterTypes()[i]);
          }
          assertEquals(type, m.invoke(null, args).getClass());
        }
      }
    }
    assertTrue(withers > 40, "found " + withers);
  }

  @Test
  void requiredRequestMembersAreChecked() {
    assertThrows(NullPointerException.class, () -> new HashRequest(null, "x"));
    assertThrows(NullPointerException.class, () -> new EncryptRequest("00", null));
    HpkeSealRequest seal =
        HpkeSealRequest.of("pk", "hello").withKem(HpkeKem.X25519).withAead(HpkeAead.AES_128_GCM);
    assertEquals(HpkeKem.X25519, seal.kem());
    assertNull(seal.psk());
  }

  @Test
  void enumsUseTheWireNames() throws Exception {
    int enums = 0;
    for (Class<?> type : Synthetic.sdkClasses()) {
      if (!type.isEnum()) {
        continue;
      }
      enums++;
      Method wire = type.getMethod("wire");
      for (Object constant : type.getEnumConstants()) {
        String json = Json.MAPPER.writeValueAsString(constant);
        assertEquals(json, Json.MAPPER.writeValueAsString(wire.invoke(constant)));
        assertEquals(constant, Json.MAPPER.readValue(json, type));
        assertFalse(constant.toString().isEmpty());
      }
    }
    assertEquals(13, enums);
    assertEquals("sha3-256", HashAlgorithm.SHA3_256.toString());
    assertEquals("65", Json.MAPPER.writeValueAsString(MlDsaLevel.ML_DSA_65));
  }

  @Test
  void bytesHelpers() {
    byte[] raw = {0, 1, (byte) 0xfe, (byte) 0xff};
    assertEquals("0001feff", Bytes.hex(raw));
    assertArrayEquals(raw, Bytes.fromHex("0001FEFF"));
    assertEquals("AAH+/w==", Bytes.base64(raw));
    assertArrayEquals(raw, Bytes.fromBase64("AAH+/w=="));
    assertNull(Bytes.fromHexOrNull(null));
    assertNull(Bytes.fromBase64OrNull(null));
    assertThrows(IllegalArgumentException.class, () -> Bytes.fromHex("abc"));
    assertThrows(IllegalArgumentException.class, () -> Bytes.fromBase64("!!!!"));
  }

  @Test
  void unknownResponseMembersAreIgnored() throws IOException {
    HashResult r =
        Decoder.data(HashResult.class)
            .decode(
                "{\"data\":{\"digest\":\"00\",\"algorithm\":\"sha256\",\"length\":1,\"new\":true}}"
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8));
    assertEquals(1, r.length());
  }
}
