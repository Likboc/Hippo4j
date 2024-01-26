package com.example.remote;

import com.example.entity.Result;

import java.util.Map;

public interface HttpAgent {
    Result httpPostByConfig(String path, Map<String,String> headers,Map<String,String> paramValue,long readTimeoutMs);
}
