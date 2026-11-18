// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Probes, service metadata, the algorithm catalogue and the compliance documents. */
abstract class MetaOperations extends ServiceOperations {

  private static final Operation<HealthResult> HEALTH =
      Operation.get("/health", false, Decoder.bare(HealthResult.class));
  private static final Operation<LiveResult> LIVE =
      Operation.get("/live", false, Decoder.bare(LiveResult.class));
  private static final Operation<ReadyResult> READY =
      Operation.get("/ready", false, Decoder.bare(ReadyResult.class));
  private static final Operation<ServiceInfo> SERVICE_INFO =
      Operation.get("/", true, Decoder.bare(ServiceInfo.class));
  private static final Operation<Map<String, List<String>>> ALGORITHMS =
      Operation.get("/v2/algorithms", true, Decoder.dataMap());
  private static final Operation<CbomDocument> GET_CBOM =
      Operation.get("/v2/compliance/cbom", true, Decoder.bare(CbomDocument.class));
  private static final Operation<DoraScorecard> GET_DORA_COMPLIANCE =
      Operation.get("/v2/compliance/dora", true, Decoder.data(DoraScorecard.class));

  MetaOperations(Transport transport) {
    super(transport);
  }

  /**
   * Health check: {@code GET /health}.
   *
   * <p>A public route: the client sends no credentials.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HealthResult> healthAsync() {
    return call(HEALTH, null);
  }

  /**
   * Health check: {@code GET /health}; waits for {@code healthAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HealthResult health() {
    return await(HEALTH.route(), healthAsync());
  }

  /**
   * Liveness probe: {@code GET /live}.
   *
   * <p>A public route: the client sends no credentials.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<LiveResult> liveAsync() {
    return call(LIVE, null);
  }

  /**
   * Liveness probe: {@code GET /live}; waits for {@code liveAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public LiveResult live() {
    return await(LIVE.route(), liveAsync());
  }

  /**
   * Readiness probe: {@code GET /ready}.
   *
   * <p>A public route: the client sends no credentials.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<ReadyResult> readyAsync() {
    return call(READY, null);
  }

  /**
   * Readiness probe: {@code GET /ready}; waits for {@code readyAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public ReadyResult ready() {
    return await(READY.route(), readyAsync());
  }

  /**
   * Service metadata: {@code GET /}.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<ServiceInfo> serviceInfoAsync() {
    return call(SERVICE_INFO, null);
  }

  /**
   * Service metadata: {@code GET /}; waits for {@code serviceInfoAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public ServiceInfo serviceInfo() {
    return await(SERVICE_INFO.route(), serviceInfoAsync());
  }

  /**
   * List supported algorithms: {@code GET /v2/algorithms}.
   *
   * @return a future completed with the algorithm names, by category, or exceptionally with an
   *     {@link ApiError} or a {@link TransportError}
   */
  public CompletableFuture<Map<String, List<String>>> algorithmsAsync() {
    return call(ALGORITHMS, null);
  }

  /**
   * List supported algorithms: {@code GET /v2/algorithms}; waits for {@code algorithmsAsync}.
   *
   * @return the algorithm names, by category
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public Map<String, List<String>> algorithms() {
    return await(ALGORITHMS.route(), algorithmsAsync());
  }

  /**
   * Cryptographic Bill of Materials (CBOM) in CycloneDX 1.6 format: {@code GET
   * /v2/compliance/cbom}.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<CbomDocument> getCbomAsync() {
    return call(GET_CBOM, null);
  }

  /**
   * Cryptographic Bill of Materials (CBOM) in CycloneDX 1.6 format: {@code GET
   * /v2/compliance/cbom}; waits for {@code getCbomAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public CbomDocument getCbom() {
    return await(GET_CBOM.route(), getCbomAsync());
  }

  /**
   * DORA cryptographic inventory (self-assessment): {@code GET /v2/compliance/dora}.
   *
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<DoraScorecard> getDoraComplianceAsync() {
    return call(GET_DORA_COMPLIANCE, null);
  }

  /**
   * DORA cryptographic inventory (self-assessment): {@code GET /v2/compliance/dora}; waits for
   * {@code getDoraComplianceAsync}.
   *
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public DoraScorecard getDoraCompliance() {
    return await(GET_DORA_COMPLIANCE.route(), getDoraComplianceAsync());
  }
}
