package com.example.tools;

import java.util.UUID;

public class IdUtil {
    public String fastUUID(){
        return UUID.randomUUID().toString();
    }

    public String simpleUUID(){
        return String.join("",fastUUID().split("-"));
    }
}
