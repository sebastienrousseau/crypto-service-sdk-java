// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A loopback HTTP server that records every request and answers through a handler. */
final class TestServer implements AutoCloseable {

  /** One request as the server saw it. */
  record Seen(String method, String path, Headers headers, byte[] body) {
    String text() {
      return new String(body, StandardCharsets.UTF_8);
    }

    String header(String name) {
      return headers.getFirst(name);
    }
  }

  /** One answer: status, headers, body, and how long to wait before sending it. */
  record Answer(int status, Map<String, String> headers, String body, Duration delay) {
    static Answer json(int status, String body) {
      String type = status >= 400 ? "application/problem+json" : "application/json";
      return new Answer(status, Map.of("Content-Type", type), body, Duration.ZERO);
    }

    Answer with(String header, String value) {
      Map<String, String> all = new java.util.HashMap<>(headers);
      all.put(header, value);
      return new Answer(status, all, body, delay);
    }

    Answer after(Duration wait) {
      return new Answer(status, headers, body, wait);
    }
  }

  /** Chooses the answer for a request; {@code index} counts requests from 0. */
  @FunctionalInterface
  interface Handler {
    Answer answer(Seen seen, int index);
  }

  private final HttpServer server;
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Handler handler;
  private final List<Seen> seen = new CopyOnWriteArrayList<>();

  TestServer(Handler handler) throws IOException {
    this.handler = handler;
    this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/", this::handle);
    server.setExecutor(executor);
    server.start();
  }

  /** Answers in script order, repeating the last answer. */
  static TestServer script(Answer... answers) throws IOException {
    return new TestServer((s, i) -> answers[Math.min(i, answers.length - 1)]);
  }

  String url() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  List<Seen> seen() {
    return seen;
  }

  CryptoServiceClient.Builder client() {
    return CryptoServiceClient.builder().baseUrl(url()).apiKey("test-api-key");
  }

  private void handle(HttpExchange exchange) throws IOException {
    Seen request =
        new Seen(
            exchange.getRequestMethod(),
            exchange.getRequestURI().getPath(),
            exchange.getRequestHeaders(),
            exchange.getRequestBody().readAllBytes());
    int index;
    synchronized (seen) {
      index = seen.size();
      seen.add(request);
    }
    Answer answer = handler.answer(request, index);
    sleep(answer.delay());
    answer.headers().forEach((k, v) -> exchange.getResponseHeaders().add(k, v));
    byte[] bytes = answer.body().getBytes(StandardCharsets.UTF_8);
    try {
      exchange.sendResponseHeaders(answer.status(), bytes.length == 0 ? -1 : bytes.length);
      try (OutputStream out = exchange.getResponseBody()) {
        out.write(bytes);
      }
    } catch (IOException e) {
      // The client went away (a cancelled or timed-out call); nothing to answer.
    }
  }

  private static void sleep(Duration delay) {
    try {
      Thread.sleep(delay.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void close() {
    server.stop(0);
    executor.shutdownNow();
  }
}
