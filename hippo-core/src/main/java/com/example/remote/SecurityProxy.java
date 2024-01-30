package com.example.remote;

import com.example.entity.BootstrapProperties;
import com.example.entity.Result;
import com.example.util.HttpUtil;
import com.example.util.JSONUtil;
import com.example.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.example.entity.TokenInfo;
@Slf4j
public class SecurityProxy {
    private static final String APPLY_TOKEN_URL = "/hippo4j/v1/cs/auth/users/apply/token";

    private final String username;

    private final String password;

    @Getter
    private String accessToken;

    private long tokenTtl;

    private long lastRefreshTime;

    private long tokenRefreshWindow;

    public SecurityProxy(BootstrapProperties properties) {
        this.username = properties.getUsername();
        this.password = properties.getPassword();
    }

    public boolean applyToken(List<String> servers) {
        try {
            if ((System.currentTimeMillis() - lastRefreshTime) < TimeUnit.SECONDS.toMillis(tokenTtl - tokenRefreshWindow)) {
                return true;
            }
            for (String server : servers) {
                if (applyToken(server)) {
                    lastRefreshTime = System.currentTimeMillis();
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public boolean applyToken(String server) {
        if (StringUtil.isAllNotEmpty(username, password)) {
            String url = server + APPLY_TOKEN_URL;
            Map<String, String> bodyMap = new HashMap(2);
            bodyMap.put("userName", username);
            bodyMap.put("password", password);
            try {
                Result result = HttpUtil.post(url, bodyMap, Result.class);
                if (!result.isSuccess()) {
                    log.error("Error getting access token. message: {}", result.getMessage());
                    return false;
                }
                String tokenJsonStr = JSONUtil.toJSONString(result.getData());
                TokenInfo tokenInfo = JSONUtil.parseObject(tokenJsonStr, TokenInfo.class);
                accessToken = tokenInfo.getAccessToken();
                tokenTtl = tokenInfo.getTokenTtl();
                tokenRefreshWindow = tokenTtl / 10;
            } catch (Throwable ex) {
                log.error("Failed to apply for token. message: {}", ex.getMessage());
                return false;
            }
        }
        return true;
    }
}
