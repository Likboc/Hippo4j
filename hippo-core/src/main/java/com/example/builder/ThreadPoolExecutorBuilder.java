package com.example.builder;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class ThreadPoolExecutorBuilder implements Builder<ThreadPoolExecutor> {

    private ThreadPoolExecutor executor;

    private int corePoolSize = Runtime.getRuntime().availableProcessors();

    private int maxPoolSize = corePoolSize + (corePoolSize >> 1);

    private BlockingQueue taskQueue;

    private long keepAliveTime = 30000L;

    private ThreadFactory threadFactory;

    private RejectedExecutionHandler rejectedExecutionHandler;
    @Override
    public ThreadPoolExecutor build() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,maxPoolSize,keepAliveTime,TimeUnit.MILLISECONDS,taskQueue,threadFactory, (ThreadPoolExecutor.RejectedExecutionHandler) rejectedExecutionHandler);
        return executor;
    }
}
