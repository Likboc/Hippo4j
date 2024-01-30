package com.example.remote;

public class AbstractHealthCheck implements ServerHealthCheck{
    @Override
    public boolean isHealthStatus() {
        return false;
    }
}
