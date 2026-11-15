// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.util.concurrent.CompletableFuture;

/** ML-KEM, hybrid X25519 + ML-KEM, ML-DSA and SLH-DSA with server-held keys. */
abstract class PostQuantumOperations extends KeyOperations {

  private static final Operation<MlKemKeyPair> ML_KEM_GENERATE_KEY_PAIR =
      Operation.post("/v2/pq/keygen", true, Decoder.data(MlKemKeyPair.class));
  private static final Operation<MlKemEncapsulateResult> ML_KEM_ENCAPSULATE =
      Operation.post("/v2/pq/encapsulate", true, Decoder.data(MlKemEncapsulateResult.class));
  private static final Operation<KemDecapsulateResult> ML_KEM_DECAPSULATE =
      Operation.post("/v2/pq/decapsulate", true, Decoder.data(KemDecapsulateResult.class));
  private static final Operation<HybridKeyPair> PQ_GENERATE_KEY_PAIR =
      Operation.post("/v2/pq/hybrid/keygen", true, Decoder.data(HybridKeyPair.class));
  private static final Operation<HybridEncapsulateResult> PQ_ENCAPSULATE =
      Operation.post(
          "/v2/pq/hybrid/encapsulate", true, Decoder.data(HybridEncapsulateResult.class));
  private static final Operation<HybridDecapsulateResult> PQ_DECAPSULATE =
      Operation.post(
          "/v2/pq/hybrid/decapsulate", true, Decoder.data(HybridDecapsulateResult.class));
  private static final Operation<MlDsaKeyPair> PQ_SIGN_KEYGEN =
      Operation.post("/v2/pq/dsa/keygen", true, Decoder.data(MlDsaKeyPair.class));
  private static final Operation<MlDsaSignResult> PQ_SIGN =
      Operation.post("/v2/pq/dsa/sign", true, Decoder.data(MlDsaSignResult.class));
  private static final Operation<MlDsaVerifyResult> PQ_VERIFY =
      Operation.post("/v2/pq/dsa/verify", true, Decoder.data(MlDsaVerifyResult.class));
  private static final Operation<SlhDsaKeyPair> PQ_HASH_SIGN_KEYGEN =
      Operation.post("/v2/pq/slh-dsa/keygen", true, Decoder.data(SlhDsaKeyPair.class));
  private static final Operation<SlhDsaSignResult> PQ_HASH_SIGN =
      Operation.post("/v2/pq/slh-dsa/sign", true, Decoder.data(SlhDsaSignResult.class));
  private static final Operation<SlhDsaVerifyResult> PQ_HASH_VERIFY =
      Operation.post("/v2/pq/slh-dsa/verify", true, Decoder.data(SlhDsaVerifyResult.class));

  PostQuantumOperations(Transport transport) {
    super(transport);
  }

  /**
   * Generate a server-held ML-KEM-768 key pair: {@code POST /v2/pq/keygen}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MlKemKeyPair> mlKemGenerateKeyPairAsync() {
    return call(ML_KEM_GENERATE_KEY_PAIR, Json.EMPTY_OBJECT);
  }

  /**
   * Generate a server-held ML-KEM-768 key pair: {@code POST /v2/pq/keygen}; waits for {@code
   * mlKemGenerateKeyPairAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MlKemKeyPair mlKemGenerateKeyPair() {
    return await(ML_KEM_GENERATE_KEY_PAIR.route(), mlKemGenerateKeyPairAsync());
  }

  /**
   * ML-KEM encapsulate: {@code POST /v2/pq/encapsulate}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MlKemEncapsulateResult> mlKemEncapsulateAsync(
      MlKemEncapsulateRequest request) {
    return call(ML_KEM_ENCAPSULATE, request);
  }

  /**
   * ML-KEM encapsulate: {@code POST /v2/pq/encapsulate}; waits for {@code mlKemEncapsulateAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MlKemEncapsulateResult mlKemEncapsulate(MlKemEncapsulateRequest request) {
    return await(ML_KEM_ENCAPSULATE.route(), mlKemEncapsulateAsync(request));
  }

  /**
   * ML-KEM decapsulate: {@code POST /v2/pq/decapsulate}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KemDecapsulateResult> mlKemDecapsulateAsync(
      MlKemDecapsulateRequest request) {
    return call(ML_KEM_DECAPSULATE, request);
  }

  /**
   * ML-KEM decapsulate: {@code POST /v2/pq/decapsulate}; waits for {@code mlKemDecapsulateAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KemDecapsulateResult mlKemDecapsulate(MlKemDecapsulateRequest request) {
    return await(ML_KEM_DECAPSULATE.route(), mlKemDecapsulateAsync(request));
  }

  /**
   * Generate a server-held hybrid X25519 + ML-KEM-768 key pair: {@code POST /v2/pq/hybrid/keygen}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HybridKeyPair> pqGenerateKeyPairAsync() {
    return call(PQ_GENERATE_KEY_PAIR, Json.EMPTY_OBJECT);
  }

  /**
   * Generate a server-held hybrid X25519 + ML-KEM-768 key pair: {@code POST /v2/pq/hybrid/keygen};
   * waits for {@code pqGenerateKeyPairAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HybridKeyPair pqGenerateKeyPair() {
    return await(PQ_GENERATE_KEY_PAIR.route(), pqGenerateKeyPairAsync());
  }

  /**
   * Hybrid encapsulate (X25519 + ML-KEM-768): {@code POST /v2/pq/hybrid/encapsulate}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HybridEncapsulateResult> pqEncapsulateAsync(
      HybridEncapsulateRequest request) {
    return call(PQ_ENCAPSULATE, request);
  }

  /**
   * Hybrid encapsulate (X25519 + ML-KEM-768): {@code POST /v2/pq/hybrid/encapsulate}; waits for
   * {@code pqEncapsulateAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HybridEncapsulateResult pqEncapsulate(HybridEncapsulateRequest request) {
    return await(PQ_ENCAPSULATE.route(), pqEncapsulateAsync(request));
  }

  /**
   * Hybrid decapsulate (X25519 + ML-KEM-768): {@code POST /v2/pq/hybrid/decapsulate}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HybridDecapsulateResult> pqDecapsulateAsync(
      HybridDecapsulateRequest request) {
    return call(PQ_DECAPSULATE, request);
  }

  /**
   * Hybrid decapsulate (X25519 + ML-KEM-768): {@code POST /v2/pq/hybrid/decapsulate}; waits for
   * {@code pqDecapsulateAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HybridDecapsulateResult pqDecapsulate(HybridDecapsulateRequest request) {
    return await(PQ_DECAPSULATE.route(), pqDecapsulateAsync(request));
  }

  /**
   * Generate a server-held ML-DSA key pair (FIPS 204): {@code POST /v2/pq/dsa/keygen}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MlDsaKeyPair> pqSignKeygenAsync(MlDsaKeygenRequest request) {
    return call(PQ_SIGN_KEYGEN, request);
  }

  /**
   * Generate a server-held ML-DSA key pair (FIPS 204): {@code POST /v2/pq/dsa/keygen}; waits for
   * {@code pqSignKeygenAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MlDsaKeyPair pqSignKeygen(MlDsaKeygenRequest request) {
    return await(PQ_SIGN_KEYGEN.route(), pqSignKeygenAsync(request));
  }

  /**
   * Sign with ML-DSA (FIPS 204): {@code POST /v2/pq/dsa/sign}.
   *
   * <p>Needs the scope {@code crypto:sign}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MlDsaSignResult> pqSignAsync(SignRequest request) {
    return call(PQ_SIGN, request);
  }

  /**
   * Sign with ML-DSA (FIPS 204): {@code POST /v2/pq/dsa/sign}; waits for {@code pqSignAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MlDsaSignResult pqSign(SignRequest request) {
    return await(PQ_SIGN.route(), pqSignAsync(request));
  }

  /**
   * Verify with ML-DSA (FIPS 204): {@code POST /v2/pq/dsa/verify}.
   *
   * <p>Needs the scope {@code crypto:verify}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MlDsaVerifyResult> pqVerifyAsync(MlDsaVerifyRequest request) {
    return call(PQ_VERIFY, request);
  }

  /**
   * Verify with ML-DSA (FIPS 204): {@code POST /v2/pq/dsa/verify}; waits for {@code pqVerifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MlDsaVerifyResult pqVerify(MlDsaVerifyRequest request) {
    return await(PQ_VERIFY.route(), pqVerifyAsync(request));
  }

  /**
   * Generate a server-held SLH-DSA key pair (FIPS 205): {@code POST /v2/pq/slh-dsa/keygen}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SlhDsaKeyPair> pqHashSignKeygenAsync(SlhDsaKeygenRequest request) {
    return call(PQ_HASH_SIGN_KEYGEN, request);
  }

  /**
   * Generate a server-held SLH-DSA key pair (FIPS 205): {@code POST /v2/pq/slh-dsa/keygen}; waits
   * for {@code pqHashSignKeygenAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SlhDsaKeyPair pqHashSignKeygen(SlhDsaKeygenRequest request) {
    return await(PQ_HASH_SIGN_KEYGEN.route(), pqHashSignKeygenAsync(request));
  }

  /**
   * Sign with SLH-DSA (FIPS 205): {@code POST /v2/pq/slh-dsa/sign}.
   *
   * <p>Needs the scope {@code crypto:sign}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SlhDsaSignResult> pqHashSignAsync(SignRequest request) {
    return call(PQ_HASH_SIGN, request);
  }

  /**
   * Sign with SLH-DSA (FIPS 205): {@code POST /v2/pq/slh-dsa/sign}; waits for {@code
   * pqHashSignAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SlhDsaSignResult pqHashSign(SignRequest request) {
    return await(PQ_HASH_SIGN.route(), pqHashSignAsync(request));
  }

  /**
   * Verify with SLH-DSA (FIPS 205): {@code POST /v2/pq/slh-dsa/verify}.
   *
   * <p>Needs the scope {@code crypto:verify}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SlhDsaVerifyResult> pqHashVerifyAsync(SlhDsaVerifyRequest request) {
    return call(PQ_HASH_VERIFY, request);
  }

  /**
   * Verify with SLH-DSA (FIPS 205): {@code POST /v2/pq/slh-dsa/verify}; waits for {@code
   * pqHashVerifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SlhDsaVerifyResult pqHashVerify(SlhDsaVerifyRequest request) {
    return await(PQ_HASH_VERIFY.route(), pqHashVerifyAsync(request));
  }
}
