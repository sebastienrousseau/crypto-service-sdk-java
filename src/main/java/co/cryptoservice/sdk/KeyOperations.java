// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.concurrent.CompletableFuture;

/**
 * Server-held keys: generation, export, deletion, Ed25519 signing, sealed boxes and multi-recipient
 * encryption.
 */
abstract class KeyOperations extends CryptoOperations {

  private static final Operation<KeyGenerateResult> GENERATE_KEY_PAIR =
      Operation.post("/v2/keys/generate", true, Decoder.data(KeyGenerateResult.class));
  private static final Operation<KeyExportResult> EXPORT_KEY =
      Operation.post("/v2/keys/export", true, Decoder.data(KeyExportResult.class));
  private static final Operation<KeyDeleteResult> DELETE_KEY =
      Operation.post("/v2/keys/delete", true, Decoder.data(KeyDeleteResult.class));
  private static final Operation<SignResult> SIGN =
      Operation.post("/v2/sign", true, Decoder.data(SignResult.class));
  private static final Operation<VerifyResult> VERIFY =
      Operation.post("/v2/verify", true, Decoder.data(VerifyResult.class));
  private static final Operation<StreamSignResult> STREAM_SIGN =
      Operation.post("/v2/stream/sign", true, Decoder.data(StreamSignResult.class));
  private static final Operation<StreamVerifyResult> STREAM_VERIFY =
      Operation.post("/v2/stream/verify", true, Decoder.data(StreamVerifyResult.class));
  private static final Operation<SealedboxSealResult> SEALEDBOX_SEAL =
      Operation.post("/v2/sealedbox/seal", true, Decoder.data(SealedboxSealResult.class));
  private static final Operation<String> SEALEDBOX_OPEN =
      Operation.post("/v2/sealedbox/open", true, Decoder.dataString());
  private static final Operation<SealedboxSealResult> SEALEDBOX_SEAL_PQ =
      Operation.post("/v2/sealedbox/seal-pq", true, Decoder.data(SealedboxSealResult.class));
  private static final Operation<String> SEALEDBOX_OPEN_PQ =
      Operation.post("/v2/sealedbox/open-pq", true, Decoder.dataString());
  private static final Operation<MultiRecipientResult> MULTI_RECIPIENT_ENCRYPT =
      Operation.post("/v2/multi-recipient/encrypt", true, Decoder.data(MultiRecipientResult.class));

  KeyOperations(Transport transport) {
    super(transport);
  }

  /**
   * Generate a server-held key pair for any supported algorithm: {@code POST /v2/keys/generate}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KeyGenerateResult> generateKeyPairAsync(KeyGenerateRequest request) {
    return call(GENERATE_KEY_PAIR, request);
  }

  /**
   * Generate a server-held key pair for any supported algorithm: {@code POST /v2/keys/generate};
   * waits for {@code generateKeyPairAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KeyGenerateResult generateKeyPair(KeyGenerateRequest request) {
    return await(GENERATE_KEY_PAIR.route(), generateKeyPairAsync(request));
  }

  /**
   * Generate a server-held key pair for any supported algorithm: {@code POST /v2/keys/generate},
   * with every option left to the server's default.
   *
   * @return a future completed with the result
   */
  public CompletableFuture<KeyGenerateResult> generateKeyPairAsync() {
    return call(GENERATE_KEY_PAIR, Json.EMPTY_OBJECT);
  }

  /**
   * Generate a server-held key pair for any supported algorithm: {@code POST /v2/keys/generate},
   * with every option left to the server's default; waits for the result.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KeyGenerateResult generateKeyPair() {
    return await(GENERATE_KEY_PAIR.route(), generateKeyPairAsync());
  }

  /**
   * Export a server-held private key: {@code POST /v2/keys/export}.
   *
   * <p>Needs the scope {@code crypto:keys:export}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KeyExportResult> exportKeyAsync(KeyIdRequest request) {
    return call(EXPORT_KEY, request);
  }

  /**
   * Export a server-held private key: {@code POST /v2/keys/export}; waits for {@code
   * exportKeyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KeyExportResult exportKey(KeyIdRequest request) {
    return await(EXPORT_KEY.route(), exportKeyAsync(request));
  }

  /**
   * Delete a server-held key: {@code POST /v2/keys/delete}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KeyDeleteResult> deleteKeyAsync(KeyIdRequest request) {
    return call(DELETE_KEY, request);
  }

  /**
   * Delete a server-held key: {@code POST /v2/keys/delete}; waits for {@code deleteKeyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KeyDeleteResult deleteKey(KeyIdRequest request) {
    return await(DELETE_KEY.route(), deleteKeyAsync(request));
  }

  /**
   * Sign with Ed25519: {@code POST /v2/sign}.
   *
   * <p>Needs the scope {@code crypto:sign}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SignResult> signAsync(SignRequest request) {
    return call(SIGN, request);
  }

  /**
   * Sign with Ed25519: {@code POST /v2/sign}; waits for {@code signAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SignResult sign(SignRequest request) {
    return await(SIGN.route(), signAsync(request));
  }

  /**
   * Verify an Ed25519 signature: {@code POST /v2/verify}.
   *
   * <p>Needs the scope {@code crypto:verify}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<VerifyResult> verifyAsync(VerifyRequest request) {
    return call(VERIFY, request);
  }

  /**
   * Verify an Ed25519 signature: {@code POST /v2/verify}; waits for {@code verifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public VerifyResult verify(VerifyRequest request) {
    return await(VERIFY.route(), verifyAsync(request));
  }

  /**
   * Batch sign messages with sub-millisecond throughput: {@code POST /v2/stream/sign}.
   *
   * <p>Needs the scope {@code crypto:sign}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<StreamSignResult> streamSignAsync(StreamSignRequest request) {
    return call(STREAM_SIGN, request);
  }

  /**
   * Batch sign messages with sub-millisecond throughput: {@code POST /v2/stream/sign}; waits for
   * {@code streamSignAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public StreamSignResult streamSign(StreamSignRequest request) {
    return await(STREAM_SIGN.route(), streamSignAsync(request));
  }

  /**
   * Batch verify digital signatures: {@code POST /v2/stream/verify}.
   *
   * <p>Needs the scope {@code crypto:verify}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<StreamVerifyResult> streamVerifyAsync(StreamVerifyRequest request) {
    return call(STREAM_VERIFY, request);
  }

  /**
   * Batch verify digital signatures: {@code POST /v2/stream/verify}; waits for {@code
   * streamVerifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public StreamVerifyResult streamVerify(StreamVerifyRequest request) {
    return await(STREAM_VERIFY.route(), streamVerifyAsync(request));
  }

  /**
   * Anonymous public-key encryption (X25519 sealed box): {@code POST /v2/sealedbox/seal}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SealedboxSealResult> sealedboxSealAsync(SealedboxSealRequest request) {
    return call(SEALEDBOX_SEAL, request);
  }

  /**
   * Anonymous public-key encryption (X25519 sealed box): {@code POST /v2/sealedbox/seal}; waits for
   * {@code sealedboxSealAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SealedboxSealResult sealedboxSeal(SealedboxSealRequest request) {
    return await(SEALEDBOX_SEAL.route(), sealedboxSealAsync(request));
  }

  /**
   * Decrypt an anonymous sealed box (X25519): {@code POST /v2/sealedbox/open}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the plaintext as UTF-8 text, or exceptionally with an {@link
   *     ApiError} or a {@link TransportError}
   */
  public CompletableFuture<String> sealedboxOpenAsync(SealedboxOpenRequest request) {
    return call(SEALEDBOX_OPEN, request);
  }

  /**
   * Decrypt an anonymous sealed box (X25519): {@code POST /v2/sealedbox/open}; waits for {@code
   * sealedboxOpenAsync}.
   *
   * @param request the request body
   * @return the plaintext as UTF-8 text
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public String sealedboxOpen(SealedboxOpenRequest request) {
    return await(SEALEDBOX_OPEN.route(), sealedboxOpenAsync(request));
  }

  /**
   * Post-quantum anonymous encryption (X25519 + ML-KEM-768 sealed box): {@code POST
   * /v2/sealedbox/seal-pq}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SealedboxSealResult> sealedboxSealPqAsync(
      SealedboxSealPqRequest request) {
    return call(SEALEDBOX_SEAL_PQ, request);
  }

  /**
   * Post-quantum anonymous encryption (X25519 + ML-KEM-768 sealed box): {@code POST
   * /v2/sealedbox/seal-pq}; waits for {@code sealedboxSealPqAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SealedboxSealResult sealedboxSealPq(SealedboxSealPqRequest request) {
    return await(SEALEDBOX_SEAL_PQ.route(), sealedboxSealPqAsync(request));
  }

  /**
   * Decrypt a post-quantum sealed box (X25519 + ML-KEM-768): {@code POST /v2/sealedbox/open-pq}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the plaintext as UTF-8 text, or exceptionally with an {@link
   *     ApiError} or a {@link TransportError}
   */
  public CompletableFuture<String> sealedboxOpenPqAsync(SealedboxOpenRequest request) {
    return call(SEALEDBOX_OPEN_PQ, request);
  }

  /**
   * Decrypt a post-quantum sealed box (X25519 + ML-KEM-768): {@code POST /v2/sealedbox/open-pq};
   * waits for {@code sealedboxOpenPqAsync}.
   *
   * @param request the request body
   * @return the plaintext as UTF-8 text
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public String sealedboxOpenPq(SealedboxOpenRequest request) {
    return await(SEALEDBOX_OPEN_PQ.route(), sealedboxOpenPqAsync(request));
  }

  /**
   * Encrypt for multiple recipients (hybrid key wrapping): {@code POST
   * /v2/multi-recipient/encrypt}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MultiRecipientResult> multiRecipientEncryptAsync(
      MultiRecipientEncryptRequest request) {
    return call(MULTI_RECIPIENT_ENCRYPT, request);
  }

  /**
   * Encrypt for multiple recipients (hybrid key wrapping): {@code POST
   * /v2/multi-recipient/encrypt}; waits for {@code multiRecipientEncryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MultiRecipientResult multiRecipientEncrypt(MultiRecipientEncryptRequest request) {
    return await(MULTI_RECIPIENT_ENCRYPT.route(), multiRecipientEncryptAsync(request));
  }
}
