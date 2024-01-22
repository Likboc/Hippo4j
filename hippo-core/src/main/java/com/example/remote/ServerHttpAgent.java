package com.example.remote;

import java.util.concurrent.*;

public class ServerHttpAgent implements HttpAgent{
    private ScheduledExecutorService executorService;
    private final long securityInfoRefreshIntervalMills;

    private SecurityProxy securityProxy;

    /**
     * token check looply
     */
    public ServerHttpAgent(){
        // initializr executor service
        this.executorService = new ScheduledThreadPoolExecutor(1,(r) -> {
            Thread thread = new Thread();
            thread.setDaemon(true);
            return thread;
        });
        // long polling interval
        this.securityInfoRefreshIntervalMills = 5;
        // long polling begins
        this.executorService.scheduleAtFixedRate(() -> {
            this.securityProxy.applyToken();
        },0,this.securityInfoRefreshIntervalMills, TimeUnit.MILLISECONDS);
    }
}
