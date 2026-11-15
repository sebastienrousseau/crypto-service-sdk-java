// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.concurrent.CompletableFuture;

/** Post-quantum streaming encryption, ISO 20022 dual-signature checks and HPKE. */
abstract class StreamOperations extends PostQuantumOperations {

  private static final Operation<StreamPqEncryptResult> STREAM_PQ_ENCRYPT =
      Operation.post("/v2/stream/pq-encrypt", true, Decoder.data(StreamPqEncryptResult.class));
  private static final Operation<StreamPqDecryptResult> STREAM_PQ_DECRYPT =
      Operation.post("/v2/stream/pq-decrypt", true, Decoder.data(StreamPqDecryptResult.class));
  private static final Operation<StreamMultiPqEncryptResult> STREAM_MULTI_PQ_ENCRYPT =
      Operation.post(
          "/v2/stream/multi-pq-encrypt", true, Decoder.data(StreamMultiPqEncryptResult.class));
  private static final Operation<StreamMultiPqDecryptResult> STREAM_MULTI_PQ_DECRYPT =
      Operation.post(
          "/v2/stream/multi-pq-decrypt", true, Decoder.data(StreamMultiPqDecryptResult.class));
  private static final Operation<Iso20022VerifyResult> ISO20022_VERIFY =
      Operation.post("/v2/stream/iso20022", true, Decoder.data(Iso20022VerifyResult.class));
  private static final Operation<HpkeKeyPair> HPKE_GENERATE_KEY_PAIR =
      Operation.post("/v2/hpke/keygen", true, Decoder.data(HpkeKeyPair.class));
  private static final Operation<HpkeSealResult> HPKE_SEAL =
      Operation.post("/v2/hpke/seal", true, Decoder.data(HpkeSealResult.class));
  private static final Operation<HpkeOpenResult> HPKE_OPEN =
      Operation.post("/v2/hpke/open", true, Decoder.data(HpkeOpenResult.class));

  StreamOperations(Transport transport) {
    super(transport);
  }

  /**
   * Encrypt stream with hybrid post-quantum AEAD: {@code POST /v2/stream/pq-encrypt}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<StreamPqEncryptResult> streamPqEncryptAsync(
      StreamPqEncryptRequest request) {
    return call(STREAM_PQ_ENCRYPT, request);
  }

  /**
   * Encrypt stream with hybrid post-quantum AEAD: {@code POST /v2/stream/pq-encrypt}; waits for
   * {@code streamPqEncryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public StreamPqEncryptResult streamPqEncrypt(StreamPqEncryptRequest request) {
    return await(STREAM_PQ_ENCRYPT.route(), streamPqEncryptAsync(request));
  }

  /**
   * Decrypt stream with server-held hybrid post-quantum key: {@code POST /v2/stream/pq-decrypt}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<StreamPqDecryptResult> streamPqDecryptAsync(
      StreamPqDecryptRequest request) {
    return call(STREAM_PQ_DECRYPT, request);
  }

  /**
   * Decrypt stream with server-held hybrid post-quantum key: {@code POST /v2/stream/pq-decrypt};
   * waits for {@code streamPqDecryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public StreamPqDecryptResult streamPqDecrypt(StreamPqDecryptRequest request) {
    return await(STREAM_PQ_DECRYPT.route(), streamPqDecryptAsync(request));
  }

  /**
   * Encrypt stream for multiple recipients with hybrid post-quantum AEAD: {@code POST
   * /v2/stream/multi-pq-encrypt}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<StreamMultiPqEncryptResult> streamMultiPqEncryptAsync(
      StreamMultiPqEncryptRequest request) {
    return call(STREAM_MULTI_PQ_ENCRYPT, request);
  }

  /**
   * Encrypt stream for multiple recipients with hybrid post-quantum AEAD: {@code POST
   * /v2/stream/multi-pq-encrypt}; waits for {@code streamMultiPqEncryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public StreamMultiPqEncryptResult streamMultiPqEncrypt(StreamMultiPqEncryptRequest request) {
    return await(STREAM_MULTI_PQ_ENCRYPT.route(), streamMultiPqEncryptAsync(request));
  }

  /**
   * Decrypt multi-recipient stream with server-held hybrid post-quantum key: {@code POST
   * /v2/stream/multi-pq-decrypt}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<StreamMultiPqDecryptResult> streamMultiPqDecryptAsync(
      StreamMultiPqDecryptRequest request) {
    return call(STREAM_MULTI_PQ_DECRYPT, request);
  }

  /**
   * Decrypt multi-recipient stream with server-held hybrid post-quantum key: {@code POST
   * /v2/stream/multi-pq-decrypt}; waits for {@code streamMultiPqDecryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public StreamMultiPqDecryptResult streamMultiPqDecrypt(StreamMultiPqDecryptRequest request) {
    return await(STREAM_MULTI_PQ_DECRYPT.route(), streamMultiPqDecryptAsync(request));
  }

  /**
   * Verify ISO 20022 post-quantum dual-signature envelope: {@code POST /v2/stream/iso20022}.
   *
   * <p>Needs the scope {@code crypto:verify}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<Iso20022VerifyResult> iso20022VerifyAsync(
      Iso20022VerifyRequest request) {
    return call(ISO20022_VERIFY, request);
  }

  /**
   * Verify ISO 20022 post-quantum dual-signature envelope: {@code POST /v2/stream/iso20022}; waits
   * for {@code iso20022VerifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public Iso20022VerifyResult iso20022Verify(Iso20022VerifyRequest request) {
    return await(ISO20022_VERIFY.route(), iso20022VerifyAsync(request));
  }

  /**
   * Generate a server-held HPKE key pair (classical or post-quantum hybrid): {@code POST
   * /v2/hpke/keygen}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HpkeKeyPair> hpkeGenerateKeyPairAsync(HpkeKeygenRequest request) {
    return call(HPKE_GENERATE_KEY_PAIR, request);
  }

  /**
   * Generate a server-held HPKE key pair (classical or post-quantum hybrid): {@code POST
   * /v2/hpke/keygen}; waits for {@code hpkeGenerateKeyPairAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HpkeKeyPair hpkeGenerateKeyPair(HpkeKeygenRequest request) {
    return await(HPKE_GENERATE_KEY_PAIR.route(), hpkeGenerateKeyPairAsync(request));
  }

  /**
   * Generate a server-held HPKE key pair (classical or post-quantum hybrid): {@code POST
   * /v2/hpke/keygen}, with every option left to the server's default.
   *
   * @return a future completed with the result
   */
  public CompletableFuture<HpkeKeyPair> hpkeGenerateKeyPairAsync() {
    return call(HPKE_GENERATE_KEY_PAIR, Json.EMPTY_OBJECT);
  }

  /**
   * Generate a server-held HPKE key pair (classical or post-quantum hybrid): {@code POST
   * /v2/hpke/keygen}, with every option left to the server's default; waits for the result.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HpkeKeyPair hpkeGenerateKeyPair() {
    return await(HPKE_GENERATE_KEY_PAIR.route(), hpkeGenerateKeyPairAsync());
  }

  /**
   * Encrypt (seal) a message using HPKE (RFC 9180): {@code POST /v2/hpke/seal}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HpkeSealResult> hpkeSealAsync(HpkeSealRequest request) {
    return call(HPKE_SEAL, request);
  }

  /**
   * Encrypt (seal) a message using HPKE (RFC 9180): {@code POST /v2/hpke/seal}; waits for {@code
   * hpkeSealAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HpkeSealResult hpkeSeal(HpkeSealRequest request) {
    return await(HPKE_SEAL.route(), hpkeSealAsync(request));
  }

  /**
   * Decrypt (open) an HPKE ciphertext with a server-held key: {@code POST /v2/hpke/open}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HpkeOpenResult> hpkeOpenAsync(HpkeOpenRequest request) {
    return call(HPKE_OPEN, request);
  }

  /**
   * Decrypt (open) an HPKE ciphertext with a server-held key: {@code POST /v2/hpke/open}; waits for
   * {@code hpkeOpenAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HpkeOpenResult hpkeOpen(HpkeOpenRequest request) {
    return await(HPKE_OPEN.route(), hpkeOpenAsync(request));
  }
}
