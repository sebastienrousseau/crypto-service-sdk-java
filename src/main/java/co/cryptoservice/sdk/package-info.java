// SPDX-License-Identifier: Apache-2.0 OR MIT
/**
 * A thin, typed Java client for the Crypto Service REST API (crypto-server).
 *
 * <p>Start with {@link co.cryptoservice.sdk.CryptoServiceClient}. Requests are records named
 * {@code ...Request}, results records named {@code ...Result} (or the key-pair and document types
 * the API returns); members carry the API's encodings (hex, base64, UTF-8 text) as strings, with
 * {@code ...Bytes()} accessors and {@link co.cryptoservice.sdk.Bytes} for {@code byte[]}. Errors
 * are {@link co.cryptoservice.sdk.ApiError} and {@link co.cryptoservice.sdk.TransportError}.
 *
 * <p>The SDK implements no cryptography: every operation is performed by the server.
 */
package co.cryptoservice.sdk;
