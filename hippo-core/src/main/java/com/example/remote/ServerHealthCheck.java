package com.example.remote;

public interface ServerHealthCheck {
    boolean isHealthStatus();
    void setHealthStatus(boolean healthStatus);
}
