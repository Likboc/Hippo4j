package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@ConfigurationProperties(
        prefix = "demo.ok"
)
@Component
public class BootstrapProperties {
    public String username;
    public String password;
    public String serverAddr;
}
