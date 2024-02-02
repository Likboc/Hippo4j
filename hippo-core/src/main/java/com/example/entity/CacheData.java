package com.example.entity;

import com.example.Listener;
import com.example.constant.Constants;
import com.example.util.Md5Util;
import com.example.wrapper.ManagerListenerWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.spi.CopyOnWrite;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
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

    private final CopyOnWriteArrayList<ManagerListenerWrapper> listeners;

    public CacheData(String tenantId, String itemId, String threadPoolId) {
        this.content = content;
        this.tenantId = tenantId;
        this.itemId = itemId;
        this.threadPoolId = threadPoolId;
        this.md5 = getMd5String(content);
        this.listeners = new CopyOnWriteArrayList();
    }

    public void checkListenerMd5() {
        for (ManagerListenerWrapper managerListenerWrapper : listeners) {
            if (!md5.equals(managerListenerWrapper.getLastCallMd5())) {
                safeNotifyListener(content, md5, managerListenerWrapper);
            }
        }
    }

    private void safeNotifyListener(String content, String md5, ManagerListenerWrapper managerListenerWrapper) {
        Listener listener = managerListenerWrapper.getListener();
        Runnable runnable = () -> {
            managerListenerWrapper.setLastCallMd5(md5);
            listener.receiveConfigInfo(content);
        };
        try {
            listener.getExecutor().execute(runnable);
        } catch (Exception ex) {
            log.error("Failed to execute listener. message: {}", ex.getMessage());
        }
    }

    public void setContent(String content) {
        this.content = content;
        this.md5 = getMd5String(this.content);
    }

    public static String getMd5String(String config) {
        return config == null ? Constants.NULL : Md5Util.md5Hex(config,Constants.ENCODE);
    }

    public boolean isInitializing() {
        return this.isInitializing;
    }

    public void addListener(Listener listener) {
        if (null == listener) {
            throw new IllegalArgumentException("Listener is null.");
        }
        ManagerListenerWrapper managerListenerWrap = new ManagerListenerWrapper(md5, listener);
        if (listeners.addIfAbsent(managerListenerWrap)) {
            log.info("Add listener status: ok, thread pool id: {}, listeners count: {}", threadPoolId, listeners.size());
        }
    }
}
