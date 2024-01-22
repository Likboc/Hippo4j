package com.example.remote;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityProxy {
    private static final String APPLY_TOKEN_URL = "/hippo4j/v1/cs/auth/users/apply/token";
    private final String username;
    private final String password;

    private String accessToken;


    public SecurityProxy(){
        this.username = null;
        this.password = null;
    }

    public boolean applyToken(){
        return false;
    }

}
