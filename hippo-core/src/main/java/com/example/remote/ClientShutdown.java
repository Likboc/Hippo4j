package com.example.remote;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientShutdown {

    @Getter
    private volatile boolean prepareClose = false;
    private final static Long TIME_OUT_SECOND = 1L;

    private static final int DEFAULT_COUNT = 1;
    private final CountDownLatch countDownLatch = new CountDownLatch(DEFAULT_COUNT);

    /**
     * Called when the application is closed.
     *
     * @throws InterruptedException
     */
    public void prepareDestroy() throws InterruptedException {
        prepareClose = true;
        countDownLatch.await(TIME_OUT_SECOND, TimeUnit.SECONDS);
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     *
     * <p>If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     *
     * <p>If the current count equals zero then nothing happens.
     */
    public void countDown() {
        countDownLatch.countDown();
    }
}
