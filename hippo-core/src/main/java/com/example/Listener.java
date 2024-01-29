package com.example;

import java.util.concurrent.Executor;

public interface Listener {
    Executor getExecutor();

    void receiveConfigInfo(String configInfo);
}
