package com.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Setter
@Getter
@ConfigurationProperties(prefix = BootstrapProperties.PREFIX)
public class BootstrapProperties implements BootstrapPropertiesInterface {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * Server address
     */
    private String serverAddr;

    /**
     * Netty server port
     */
    private String nettyServerPort;

    /**
     * Report type
     */
    private String reportType;

    /**
     * Namespace
     */
    private String namespace;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Whether to enable dynamic thread pool
     */
    private Boolean enable = true;

    /**
     * Print dynamic thread pool banner
     */
    private Boolean banner = true;

    /**
     * Thread pool monitoring related configuration.
     */
    private MonitorProperties monitor = new MonitorProperties();

    /***
     * Latest use {@link MonitorProperties#getEnable()}
     */
    @Deprecated
    private Boolean collect = Boolean.TRUE;

    /**
     * Latest use {@link MonitorProperties#getCollectTypes()}
     */
    @Deprecated
    private String collectType;

    /**
     * Latest use {@link MonitorProperties#getInitialDelay()}
     */
    @Deprecated
    private Long initialDelay = 10000L;

    /**
     * Latest use {@link MonitorProperties#getCollectInterval()}
     */
    @Deprecated
    private Long collectInterval = 5000L;

    /**
     * Latest use {@link MonitorProperties#getTaskBufferSize()}
     */
    @Deprecated
    private Integer taskBufferSize = 4096;
}