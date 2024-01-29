package com.example.remote;

import com.example.entity.Result;

import java.util.Map;

public interface HttpAgent {
    void start();
    Result httpPostByConfig(String path, Map<String,String> headers,Map<String,String> paramValue,long readTimeoutMs);
    Result httpGetByConfig(String path,Map<String,String > headers,Map<String,String> paramValues,long readTimeoutMs);
}
