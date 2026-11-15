// SPDX-License-Identifier: Apache-2.0 OR MIT
package co.cryptoservice.sdk;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The one timer thread the SDK uses, for retry waits and deadlines: a daemon, so it never keeps
 * the JVM alive. Cancelled tasks are removed at once, so a deadline that did not fire holds no
 * memory. Tasks only start a non-blocking send or complete a future.
 */
final class Scheduler {

  private static final ScheduledThreadPoolExecutor TIMER = timer();

  private Scheduler() {}

  private static ScheduledThreadPoolExecutor timer() {
    ScheduledThreadPoolExecutor timer =
        new ScheduledThreadPoolExecutor(
            1,
            task -> {
              Thread thread = new Thread(task, "crypto-service-sdk-timer");
              thread.setDaemon(true);
              return thread;
            });
    timer.setRemoveOnCancelPolicy(true);
    return timer;
  }

  /** Runs {@code task} after {@code delay}. */
  static ScheduledFuture<?> after(Duration delay, Runnable task) {
    return TIMER.schedule(task, delay.toNanos(), TimeUnit.NANOSECONDS);
  }
}
