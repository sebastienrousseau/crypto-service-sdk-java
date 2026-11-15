// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.concurrent.CompletableFuture;

/** KMS (envelope encryption with managed keys) and OPAQUE. */
abstract class ServiceOperations extends StreamOperations {

  private static final Operation<KmsKey> KMS_CREATE_KEY =
      Operation.post("/v2/kms/create-key", true, Decoder.data(KmsKey.class));
  private static final Operation<KmsWrapResult> KMS_WRAP =
      Operation.post("/v2/kms/wrap", true, Decoder.data(KmsWrapResult.class));
  private static final Operation<KmsUnwrapResult> KMS_UNWRAP =
      Operation.post("/v2/kms/unwrap", true, Decoder.data(KmsUnwrapResult.class));
  private static final Operation<KmsDataKey> KMS_GENERATE_DATA_KEY =
      Operation.post("/v2/kms/generate-data-key", true, Decoder.data(KmsDataKey.class));
  private static final Operation<KmsEncryptResult> KMS_ENCRYPT =
      Operation.post("/v2/kms/encrypt", true, Decoder.data(KmsEncryptResult.class));
  private static final Operation<KmsDecryptResult> KMS_DECRYPT =
      Operation.post("/v2/kms/decrypt", true, Decoder.data(KmsDecryptResult.class));
  private static final Operation<OpaqueRegisterInitResult> OPAQUE_REGISTER_INIT =
      Operation.post(
          "/v2/opaque/register/init", true, Decoder.data(OpaqueRegisterInitResult.class));
  private static final Operation<OpaqueRegisterFinishResult> OPAQUE_REGISTER_FINISH =
      Operation.post(
          "/v2/opaque/register/finish", true, Decoder.data(OpaqueRegisterFinishResult.class));
  private static final Operation<OpaqueLoginInitResult> OPAQUE_LOGIN_INIT =
      Operation.post("/v2/opaque/login/init", false, Decoder.data(OpaqueLoginInitResult.class));
  private static final Operation<OpaqueLoginFinishResult> OPAQUE_LOGIN_FINISH =
      Operation.post("/v2/opaque/login/finish", false, Decoder.data(OpaqueLoginFinishResult.class));

  ServiceOperations(Transport transport) {
    super(transport);
  }

  /**
   * Create a managed key in the designated KMS provider: {@code POST /v2/kms/create-key}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KmsKey> kmsCreateKeyAsync(KmsCreateKeyRequest request) {
    return call(KMS_CREATE_KEY, request);
  }

  /**
   * Create a managed key in the designated KMS provider: {@code POST /v2/kms/create-key}; waits for
   * {@link #kmsCreateKeyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsKey kmsCreateKey(KmsCreateKeyRequest request) {
    return await(KMS_CREATE_KEY.route(), kmsCreateKeyAsync(request));
  }

  /**
   * Create a managed key in the designated KMS provider: {@code POST /v2/kms/create-key}, with
   * every option left to the server's default.
   *
   * @return a future completed with the result
   */
  public CompletableFuture<KmsKey> kmsCreateKeyAsync() {
    return call(KMS_CREATE_KEY, Json.EMPTY_OBJECT);
  }

  /**
   * Create a managed key in the designated KMS provider: {@code POST /v2/kms/create-key}, with
   * every option left to the server's default; waits for the result.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsKey kmsCreateKey() {
    return await(KMS_CREATE_KEY.route(), kmsCreateKeyAsync());
  }

  /**
   * Wrap an existing key using a managed KMS key: {@code POST /v2/kms/wrap}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KmsWrapResult> kmsWrapAsync(KmsWrapRequest request) {
    return call(KMS_WRAP, request);
  }

  /**
   * Wrap an existing key using a managed KMS key: {@code POST /v2/kms/wrap}; waits for {@link
   * #kmsWrapAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsWrapResult kmsWrap(KmsWrapRequest request) {
    return await(KMS_WRAP.route(), kmsWrapAsync(request));
  }

  /**
   * Unwrap a wrapped key using a managed KMS key: {@code POST /v2/kms/unwrap}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KmsUnwrapResult> kmsUnwrapAsync(KmsUnwrapRequest request) {
    return call(KMS_UNWRAP, request);
  }

  /**
   * Unwrap a wrapped key using a managed KMS key: {@code POST /v2/kms/unwrap}; waits for {@link
   * #kmsUnwrapAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsUnwrapResult kmsUnwrap(KmsUnwrapRequest request) {
    return await(KMS_UNWRAP.route(), kmsUnwrapAsync(request));
  }

  /**
   * Generate a Data Encryption Key (DEK) via KMS provider: {@code POST /v2/kms/generate-data-key}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KmsDataKey> kmsGenerateDataKeyAsync(KmsGenerateDataKeyRequest request) {
    return call(KMS_GENERATE_DATA_KEY, request);
  }

  /**
   * Generate a Data Encryption Key (DEK) via KMS provider: {@code POST /v2/kms/generate-data-key};
   * waits for {@link #kmsGenerateDataKeyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsDataKey kmsGenerateDataKey(KmsGenerateDataKeyRequest request) {
    return await(KMS_GENERATE_DATA_KEY.route(), kmsGenerateDataKeyAsync(request));
  }

  /**
   * Encrypt plaintext with a managed KMS key: {@code POST /v2/kms/encrypt}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KmsEncryptResult> kmsEncryptAsync(KmsEncryptRequest request) {
    return call(KMS_ENCRYPT, request);
  }

  /**
   * Encrypt plaintext with a managed KMS key: {@code POST /v2/kms/encrypt}; waits for {@link
   * #kmsEncryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsEncryptResult kmsEncrypt(KmsEncryptRequest request) {
    return await(KMS_ENCRYPT.route(), kmsEncryptAsync(request));
  }

  /**
   * Decrypt ciphertext with a managed KMS key: {@code POST /v2/kms/decrypt}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KmsDecryptResult> kmsDecryptAsync(KmsDecryptRequest request) {
    return call(KMS_DECRYPT, request);
  }

  /**
   * Decrypt ciphertext with a managed KMS key: {@code POST /v2/kms/decrypt}; waits for {@link
   * #kmsDecryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KmsDecryptResult kmsDecrypt(KmsDecryptRequest request) {
    return await(KMS_DECRYPT.route(), kmsDecryptAsync(request));
  }

  /**
   * Initiate OPAQUE credential registration: {@code POST /v2/opaque/register/init}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<OpaqueRegisterInitResult> opaqueRegisterInitAsync(
      OpaqueRegisterInitRequest request) {
    return call(OPAQUE_REGISTER_INIT, request);
  }

  /**
   * Initiate OPAQUE credential registration: {@code POST /v2/opaque/register/init}; waits for
   * {@link #opaqueRegisterInitAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public OpaqueRegisterInitResult opaqueRegisterInit(OpaqueRegisterInitRequest request) {
    return await(OPAQUE_REGISTER_INIT.route(), opaqueRegisterInitAsync(request));
  }

  /**
   * Finish OPAQUE credential registration: {@code POST /v2/opaque/register/finish}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<OpaqueRegisterFinishResult> opaqueRegisterFinishAsync(
      OpaqueRegisterFinishRequest request) {
    return call(OPAQUE_REGISTER_FINISH, request);
  }

  /**
   * Finish OPAQUE credential registration: {@code POST /v2/opaque/register/finish}; waits for
   * {@link #opaqueRegisterFinishAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public OpaqueRegisterFinishResult opaqueRegisterFinish(OpaqueRegisterFinishRequest request) {
    return await(OPAQUE_REGISTER_FINISH.route(), opaqueRegisterFinishAsync(request));
  }

  /**
   * Initiate OPAQUE login handshake: {@code POST /v2/opaque/login/init}.
   *
   * <p>A public route: the client sends no credentials.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<OpaqueLoginInitResult> opaqueLoginInitAsync(
      OpaqueLoginInitRequest request) {
    return call(OPAQUE_LOGIN_INIT, request);
  }

  /**
   * Initiate OPAQUE login handshake: {@code POST /v2/opaque/login/init}; waits for {@link
   * #opaqueLoginInitAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public OpaqueLoginInitResult opaqueLoginInit(OpaqueLoginInitRequest request) {
    return await(OPAQUE_LOGIN_INIT.route(), opaqueLoginInitAsync(request));
  }

  /**
   * Finish OPAQUE login handshake: {@code POST /v2/opaque/login/finish}.
   *
   * <p>A public route: the client sends no credentials.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<OpaqueLoginFinishResult> opaqueLoginFinishAsync(
      OpaqueLoginFinishRequest request) {
    return call(OPAQUE_LOGIN_FINISH, request);
  }

  /**
   * Finish OPAQUE login handshake: {@code POST /v2/opaque/login/finish}; waits for {@link
   * #opaqueLoginFinishAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public OpaqueLoginFinishResult opaqueLoginFinish(OpaqueLoginFinishRequest request) {
    return await(OPAQUE_LOGIN_FINISH.route(), opaqueLoginFinishAsync(request));
  }
}
