package com.example.entity;

import lombok.Getter;
import lombok.Setter;

public class CacheData {
    @Getter
    public volatile String md5;

    public volatile String content;
    /**
     * thread pool config
     */
    public final String tenantId;

    public final String itemId;

    public final String threadPoolId;

    @Setter
    private volatile boolean isInitializing = true;

    public CacheData(String content, String tenantId, String itemId, String threadPoolId) {
        this.content = content;
        this.tenantId = tenantId;
        this.itemId = itemId;
        this.threadPoolId = threadPoolId;
    }

    public boolean isInitializing() {
        return this.isInitializing;
    }
}
