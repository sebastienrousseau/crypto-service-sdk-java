// SPDX-License-Identifier: Apache-2.0 OR MIT
// SPDX-FileCopyrightText: 2026 Sebastien Rousseau
package co.cryptoservice.sdk;

import java.util.concurrent.CompletableFuture;

/** Symmetric encryption, hashing, MACs, key derivation, password hashing and AES key wrap. */
abstract class CryptoOperations extends ClientCore {

  private static final Operation<AeadResult> ENCRYPT =
      Operation.post("/v2/encrypt", true, Decoder.data(AeadResult.class));
  private static final Operation<DecryptResult> DECRYPT =
      Operation.post("/v2/decrypt", true, Decoder.data(DecryptResult.class));
  private static final Operation<SecretboxSealResult> SECRETBOX_SEAL =
      Operation.post("/v2/secretbox/seal", true, Decoder.data(SecretboxSealResult.class));
  private static final Operation<String> SECRETBOX_OPEN =
      Operation.post("/v2/secretbox/open", true, Decoder.dataString());
  private static final Operation<PasswordEncryptResult> PASSWORD_ENCRYPT =
      Operation.post("/v2/password/encrypt", true, Decoder.data(PasswordEncryptResult.class));
  private static final Operation<String> PASSWORD_DECRYPT =
      Operation.post("/v2/password/decrypt", true, Decoder.dataString());
  private static final Operation<KeyWrapResult> KEY_WRAP =
      Operation.post("/v2/keys/wrap", true, Decoder.data(KeyWrapResult.class));
  private static final Operation<String> KEY_UNWRAP =
      Operation.post("/v2/keys/unwrap", true, Decoder.dataString());
  private static final Operation<HashResult> HASH =
      Operation.post("/v2/hash", true, Decoder.data(HashResult.class));
  private static final Operation<MacResult> MAC =
      Operation.post("/v2/hmac", true, Decoder.data(MacResult.class));
  private static final Operation<MacVerifyResult> MAC_VERIFY =
      Operation.post("/v2/hmac/verify", true, Decoder.data(MacVerifyResult.class));
  private static final Operation<KdfResult> KDF =
      Operation.post("/v2/kdf", true, Decoder.data(KdfResult.class));
  private static final Operation<PasswordHashResult> PASSWORD_HASH =
      Operation.post("/v2/password/hash", true, Decoder.data(PasswordHashResult.class));
  private static final Operation<PasswordVerifyResult> PASSWORD_VERIFY =
      Operation.post("/v2/password/verify", true, Decoder.data(PasswordVerifyResult.class));

  CryptoOperations(Transport transport) {
    super(transport);
  }

  /**
   * Encrypt with XChaCha20-Poly1305: {@code POST /v2/encrypt}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<AeadResult> encryptAsync(EncryptRequest request) {
    return call(ENCRYPT, request);
  }

  /**
   * Encrypt with XChaCha20-Poly1305: {@code POST /v2/encrypt}; waits for {@code encryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public AeadResult encrypt(EncryptRequest request) {
    return await(ENCRYPT.route(), encryptAsync(request));
  }

  /**
   * Decrypt with XChaCha20-Poly1305: {@code POST /v2/decrypt}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<DecryptResult> decryptAsync(DecryptRequest request) {
    return call(DECRYPT, request);
  }

  /**
   * Decrypt with XChaCha20-Poly1305: {@code POST /v2/decrypt}; waits for {@code decryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public DecryptResult decrypt(DecryptRequest request) {
    return await(DECRYPT.route(), decryptAsync(request));
  }

  /**
   * Encrypt with XChaCha20-Poly1305 (secretbox seal): {@code POST /v2/secretbox/seal}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<SecretboxSealResult> secretboxSealAsync(SecretboxSealRequest request) {
    return call(SECRETBOX_SEAL, request);
  }

  /**
   * Encrypt with XChaCha20-Poly1305 (secretbox seal): {@code POST /v2/secretbox/seal}; waits for
   * {@code secretboxSealAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public SecretboxSealResult secretboxSeal(SecretboxSealRequest request) {
    return await(SECRETBOX_SEAL.route(), secretboxSealAsync(request));
  }

  /**
   * Decrypt with XChaCha20-Poly1305 (secretbox open): {@code POST /v2/secretbox/open}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the plaintext as UTF-8 text, or exceptionally with an {@link
   *     ApiError} or a {@link TransportError}
   */
  public CompletableFuture<String> secretboxOpenAsync(SecretboxOpenRequest request) {
    return call(SECRETBOX_OPEN, request);
  }

  /**
   * Decrypt with XChaCha20-Poly1305 (secretbox open): {@code POST /v2/secretbox/open}; waits for
   * {@code secretboxOpenAsync}.
   *
   * @param request the request body
   * @return the plaintext as UTF-8 text
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public String secretboxOpen(SecretboxOpenRequest request) {
    return await(SECRETBOX_OPEN.route(), secretboxOpenAsync(request));
  }

  /**
   * Encrypt with password (Argon2id + XChaCha20-Poly1305): {@code POST /v2/password/encrypt}.
   *
   * <p>Needs the scope {@code crypto:encrypt}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<PasswordEncryptResult> passwordEncryptAsync(
      PasswordEncryptRequest request) {
    return call(PASSWORD_ENCRYPT, request);
  }

  /**
   * Encrypt with password (Argon2id + XChaCha20-Poly1305): {@code POST /v2/password/encrypt}; waits
   * for {@code passwordEncryptAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public PasswordEncryptResult passwordEncrypt(PasswordEncryptRequest request) {
    return await(PASSWORD_ENCRYPT.route(), passwordEncryptAsync(request));
  }

  /**
   * Decrypt with password (Argon2id + XChaCha20-Poly1305): {@code POST /v2/password/decrypt}.
   *
   * <p>Needs the scope {@code crypto:decrypt}.
   *
   * @param request the request body
   * @return a future completed with the plaintext as UTF-8 text, or exceptionally with an {@link
   *     ApiError} or a {@link TransportError}
   */
  public CompletableFuture<String> passwordDecryptAsync(PasswordDecryptRequest request) {
    return call(PASSWORD_DECRYPT, request);
  }

  /**
   * Decrypt with password (Argon2id + XChaCha20-Poly1305): {@code POST /v2/password/decrypt}; waits
   * for {@code passwordDecryptAsync}.
   *
   * @param request the request body
   * @return the plaintext as UTF-8 text
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public String passwordDecrypt(PasswordDecryptRequest request) {
    return await(PASSWORD_DECRYPT.route(), passwordDecryptAsync(request));
  }

  /**
   * Wrap a key using AES-KW or AES-KWP (RFC 3394/5649): {@code POST /v2/keys/wrap}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KeyWrapResult> keyWrapAsync(KeyWrapRequest request) {
    return call(KEY_WRAP, request);
  }

  /**
   * Wrap a key using AES-KW or AES-KWP (RFC 3394/5649): {@code POST /v2/keys/wrap}; waits for
   * {@code keyWrapAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KeyWrapResult keyWrap(KeyWrapRequest request) {
    return await(KEY_WRAP.route(), keyWrapAsync(request));
  }

  /**
   * Unwrap a key using AES-KW or AES-KWP (RFC 3394/5649): {@code POST /v2/keys/unwrap}.
   *
   * <p>Needs the scope {@code crypto:keys}.
   *
   * @param request the request body
   * @return a future completed with the unwrapped key as hex, or exceptionally with an {@link
   *     ApiError} or a {@link TransportError}
   */
  public CompletableFuture<String> keyUnwrapAsync(KeyUnwrapRequest request) {
    return call(KEY_UNWRAP, request);
  }

  /**
   * Unwrap a key using AES-KW or AES-KWP (RFC 3394/5649): {@code POST /v2/keys/unwrap}; waits for
   * {@code keyUnwrapAsync}.
   *
   * @param request the request body
   * @return the unwrapped key as hex
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public String keyUnwrap(KeyUnwrapRequest request) {
    return await(KEY_UNWRAP.route(), keyUnwrapAsync(request));
  }

  /**
   * Compute a cryptographic hash: {@code POST /v2/hash}.
   *
   * <p>Needs the scope {@code crypto:hash}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<HashResult> hashAsync(HashRequest request) {
    return call(HASH, request);
  }

  /**
   * Compute a cryptographic hash: {@code POST /v2/hash}; waits for {@code hashAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public HashResult hash(HashRequest request) {
    return await(HASH.route(), hashAsync(request));
  }

  /**
   * Compute an HMAC: {@code POST /v2/hmac}.
   *
   * <p>Needs the scope {@code crypto:sign}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MacResult> macAsync(MacRequest request) {
    return call(MAC, request);
  }

  /**
   * Compute an HMAC: {@code POST /v2/hmac}; waits for {@code macAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MacResult mac(MacRequest request) {
    return await(MAC.route(), macAsync(request));
  }

  /**
   * Verify an HMAC: {@code POST /v2/hmac/verify}.
   *
   * <p>Needs the scope {@code crypto:verify}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<MacVerifyResult> macVerifyAsync(MacVerifyRequest request) {
    return call(MAC_VERIFY, request);
  }

  /**
   * Verify an HMAC: {@code POST /v2/hmac/verify}; waits for {@code macVerifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public MacVerifyResult macVerify(MacVerifyRequest request) {
    return await(MAC_VERIFY.route(), macVerifyAsync(request));
  }

  /**
   * Derive a key from a password: {@code POST /v2/kdf}.
   *
   * <p>Needs the scope {@code crypto:kdf}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<KdfResult> kdfAsync(KdfRequest request) {
    return call(KDF, request);
  }

  /**
   * Derive a key from a password: {@code POST /v2/kdf}; waits for {@code kdfAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public KdfResult kdf(KdfRequest request) {
    return await(KDF.route(), kdfAsync(request));
  }

  /**
   * Hash a password with Argon2id: {@code POST /v2/password/hash}.
   *
   * <p>Needs the scope {@code crypto:kdf}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<PasswordHashResult> passwordHashAsync(PasswordHashRequest request) {
    return call(PASSWORD_HASH, request);
  }

  /**
   * Hash a password with Argon2id: {@code POST /v2/password/hash}; waits for {@code
   * passwordHashAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public PasswordHashResult passwordHash(PasswordHashRequest request) {
    return await(PASSWORD_HASH.route(), passwordHashAsync(request));
  }

  /**
   * Verify a password against an Argon2id hash: {@code POST /v2/password/verify}.
   *
   * <p>Needs the scope {@code crypto:kdf}.
   *
   * @param request the request body
   * @return a future completed with the result, or exceptionally with an {@link ApiError} or a
   *     {@link TransportError}
   */
  public CompletableFuture<PasswordVerifyResult> passwordVerifyAsync(
      PasswordVerifyRequest request) {
    return call(PASSWORD_VERIFY, request);
  }

  /**
   * Verify a password against an Argon2id hash: {@code POST /v2/password/verify}; waits for {@code
   * passwordVerifyAsync}.
   *
   * @param request the request body
   * @return the result
   * @throws ApiError when the server answers with an error status
   * @throws TransportError when no usable response arrives
   */
  public PasswordVerifyResult passwordVerify(PasswordVerifyRequest request) {
    return await(PASSWORD_VERIFY.route(), passwordVerifyAsync(request));
  }
}
