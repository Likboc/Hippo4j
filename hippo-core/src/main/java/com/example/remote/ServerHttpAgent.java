package com.example.remote;

import com.example.api.ApplicationContextHolder;
import com.example.constant.Constants;
import com.example.entity.BootstrapProperties;
import com.example.entity.Result;
import com.example.util.HttpUtil;
import com.example.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ServerHttpAgent implements HttpAgent {

    private final BootstrapProperties dynamicThreadPoolProperties;

    private final ServerListManager serverListManager;

    private SecurityProxy securityProxy;

    private ServerHealthCheck serverHealthCheck;

    private ScheduledExecutorService executorService;

    private final long securityInfoRefreshIntervalMills = TimeUnit.SECONDS.toMillis(5);

    /**
     * token check looply
     */
    public ServerHttpAgent(BootstrapProperties properties) {
        this.dynamicThreadPoolProperties = properties;
        this.serverListManager = new ServerListManager(dynamicThreadPoolProperties);
        this.securityProxy = new SecurityProxy(properties);
        this.securityProxy.applyToken(this.serverListManager.getServerUrls());
        // initializr executor service
        this.executorService = new ScheduledThreadPoolExecutor(1,(r) -> {
            Thread thread = new Thread();
            thread.setDaemon(true);
            return thread;
        });
        // long polling begins
        this.executorService.scheduleAtFixedRate(() -> {
            this.securityProxy.applyToken(serverListManager.getServerUrls());
        },0,this.securityInfoRefreshIntervalMills, TimeUnit.MILLISECONDS);
    }

    @Override
    public void start() {

    }

    @Override
    public String getEncode() {
        return null;
    }

    @Override
    public Result httpGetSimple(String path) {
        return null;
    }

    @Override
    public Result httpPost(String path, Object body) {
        isHealthStatus();
        path = injectSecurityInfoByPath(path);
        return HttpUtil.post(buildUrl(path), body, Result.class);
    }

    @Override
    public Result httpPostByDiscovery(String path, Object body) {
        isHealthStatus();
        path = injectSecurityInfoByPath(path);
        return HttpUtil.post(buildUrl(path), body, Result.class);
    }

    @Override
    public String getTenantId() {
        return dynamicThreadPoolProperties.getNamespace();
    }

    @Override
    public Result httpGetByConfig(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        isHealthStatus();
        injectSecurityInfo(paramValues);
        return HttpUtil.get(buildUrl(path), headers, paramValues, readTimeoutMs, Result.class);
    }

    @Override
    public Result httpPostByConfig(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        isHealthStatus();
        injectSecurityInfo(paramValues);
        return HttpUtil.post(buildUrl(path), headers, paramValues, readTimeoutMs, Result.class);
    }

    @Override
    public Result httpDeleteByConfig(String path, Map<String, String> headers, Map<String, String> paramValues, long readTimeoutMs) {
        return null;
    }

    private String buildUrl(String path) {
        return serverListManager.getCurrentServerAddr() + path;
    }

    private void isHealthStatus() {
        if (serverHealthCheck == null) {
            serverHealthCheck = ApplicationContextHolder.getBean(ServerHealthCheck.class);
        }
        serverHealthCheck.isHealthStatus();
    }

    private Map injectSecurityInfo(Map<String, String> params) {
        if (StringUtil.isNotBlank(securityProxy.getAccessToken())) {
            params.put(Constants.ACCESS_TOKEN, securityProxy.getAccessToken());
        }
        return params;
    }

    @Deprecated
    private String injectSecurityInfoByPath(String path) {
        String resultPath = HttpUtil.buildUrl(path, injectSecurityInfo(new HashMap<>()));
        return resultPath;
    }
}
