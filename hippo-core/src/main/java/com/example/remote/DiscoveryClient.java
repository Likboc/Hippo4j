package com.example.remote;

import com.example.api.ApplicationContextHolder;
import com.example.builder.ThreadFactoryBuilder;
import com.example.constant.Constants;
import com.example.entity.InstanceInfo;
import com.example.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.constant.Constants.BASE_PATH;

@Slf4j
public class DiscoveryClient implements DisposableBean {


    private final ScheduledExecutorService scheduler;
    private final HttpAgent httpAgent;
    private final InstanceInfo instanceInfo;
    private final ClientShutdown hippo4jClientShutdown;

    private volatile long lastSuccessfulHeartbeatTimestamp = -1;
    private static final String PREFIX = "DiscoveryClient_";
    private final String appPathIdentifier;

    public DiscoveryClient(HttpAgent httpAgent, InstanceInfo instanceInfo, ClientShutdown hippo4jClientShutdown) {
        this.httpAgent = httpAgent;
        this.instanceInfo = instanceInfo;
        this.hippo4jClientShutdown = hippo4jClientShutdown;
        this.appPathIdentifier = instanceInfo.getAppName().toUpperCase() + "/" + instanceInfo.getInstanceId();
        this.scheduler = new ScheduledThreadPoolExecutor(
                1,
                ThreadFactoryBuilder.builder().daemon(true).prefix("client.discovery.scheduler").build());
        register();
        // Init the schedule tasks.
        initScheduledTasks();
    }

    private void initScheduledTasks() {
        scheduler.scheduleWithFixedDelay(new HeartbeatThread(), 30, 30, TimeUnit.SECONDS);
    }

    boolean register() {
        log.info("{}{} - registering service...", PREFIX, appPathIdentifier);
        String urlPath = BASE_PATH + "/apps/register/";
        Result registerResult;
        try {
            registerResult = httpAgent.httpPostByDiscovery(urlPath, instanceInfo);
        } catch (Exception ex) {
            registerResult = Results.failure(ErrorCodeEnum.SERVICE_ERROR);
            log.error("{}{} - registration failed: {}", PREFIX, appPathIdentifier, ex.getMessage());
        }
        if (log.isInfoEnabled()) {
            log.info("{}{} - registration status: {}", PREFIX, appPathIdentifier, registerResult.isSuccess() ? "success" : "fail");
        }
        return registerResult.isSuccess();
    }

    @Override
    public void destroy() throws Exception {
        log.info("{}{} - destroy service...", PREFIX, appPathIdentifier);
        String clientCloseUrlPath = Constants.BASE_PATH + "/client/close";
        Result clientCloseResult;
        try {
            this.prepareDestroy();
            String groupKeyIp = new StringBuilder()
                    .append(instanceInfo.getGroupKey())
                    .append(Constants.GROUP_KEY_DELIMITER)
                    .append(instanceInfo.getIdentify())
                    .toString();
            ClientCloseHookExecute.ClientCloseHookReq clientCloseHookReq = new ClientCloseHookExecute.ClientCloseHookReq();
            clientCloseHookReq.setAppName(instanceInfo.getAppName())
                    .setInstanceId(instanceInfo.getInstanceId())
                    .setGroupKey(groupKeyIp);
            clientCloseResult = httpAgent.httpPostByDiscovery(clientCloseUrlPath, clientCloseHookReq);
            if (clientCloseResult.isSuccess()) {
                log.info("{}{} - client close hook success.", PREFIX, appPathIdentifier);
            }
        } catch (Throwable ex) {
            if (ex instanceof ShutdownExecuteException) {
                return;
            }
            log.error("{}{} - client close hook fail.", PREFIX, appPathIdentifier, ex);
        }
    }

    private void prepareDestroy() throws InterruptedException {
        scheduler.shutdownNow();
        // Try to make sure the ClientWorker is closed first.
        hippo4jClientShutdown.prepareDestroy();
    }

    public class HeartbeatThread implements Runnable {

        @Override
        public void run() {
            if (renew()) {
                lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
            }
        }
    }

    private boolean renew() {
        Result renewResult;
        try {
            if (scheduler.isShutdown()) {
                return false;
            }
            InstanceInfo.InstanceRenew instanceRenew = new InstanceInfo.InstanceRenew()
                    .setAppName(instanceInfo.getAppName())
                    .setInstanceId(instanceInfo.getInstanceId())
                    .setLastDirtyTimestamp(instanceInfo.getLastDirtyTimestamp().toString())
                    .setStatus(instanceInfo.getStatus().toString());
            renewResult = httpAgent.httpPostByDiscovery(BASE_PATH + "/apps/renew", instanceRenew);
            if (Objects.equals(ErrorCodeEnum.NOT_FOUND.getCode(), renewResult.getCode())) {
                long timestamp = instanceInfo.setIsDirtyWithTime();
                boolean success = register();
                // TODO Abstract server registration logic
                ThreadPoolAdapterRegister adapterRegister = ApplicationContextHolder.getBean(ThreadPoolAdapterRegister.class);
                adapterRegister.register();
                if (success) {
                    instanceInfo.unsetIsDirty(timestamp);
                }
                return success;
            }
            return renewResult.isSuccess();
        } catch (Exception ex) {
            log.error(PREFIX + "{} - was unable to send heartbeat!", appPathIdentifier, ex);
            return false;
        }
    }
}
