package com.example.remote;

import com.example.entity.CacheData;
import com.example.constant.Constants;
import com.example.entity.Result;
import com.example.tools.GroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.*;

/**
 * send check request to server continuously
 */
@Slf4j
public class ClientWorker implements DisposableBean {
    private final long timeout;
    private final String identify;
    private final HttpAgent agent;
    private final ServerHealthCheck serverHealthCheck;
    private final ScheduledExecutorService executorService;
    private final CountDownLatch cacheCondition = new CountDownLatch(1);
    private final ConcurrentHashMap<String, CacheData> cacheMap = new ConcurrentHashMap<>(16);
    public ClientWorker(HttpAgent httpAgent,
                        String identify,
                        ServerHealthCheck serverHealthCheck,
                        String version,
                        ClientShutdown clientShutdown) {
        this.agent = httpAgent;
        this.identify = identify;
        this.timeout = Constants.CONFIG_LONG_POLL_TIMEOUT;
        this.serverHealthCheck = serverHealthCheck;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, runnable -> {
           Thread thread = new Thread(runnable);
           thread.setDaemon(true);
           thread.setName("client.worker.executor");
           return thread;
        });
        this.executorService = Executors.newSingleThreadScheduledExecutor( runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("client.long.polling.executor");
            return thread;
        });
        executor.schedule(() -> {
            try{
                executorService.execute(new LongPollingRunnable(cacheMap.isEmpty(), cacheCondition));
            } catch (Throwable ex) {
                System.out.println("hi");
            }
        },1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

    class LongPollingRunnable implements Runnable {
        private boolean cacheMapInitEmptyFlag;
        private final CountDownLatch cacheCondition;
        public LongPollingRunnable(boolean cacheMapInitEmptyFlag, CountDownLatch cacheCondition) {
            this.cacheMapInitEmptyFlag = cacheMapInitEmptyFlag;
            this.cacheCondition = cacheCondition;
        }

        /**
         * firstly, run health check of the server
         * then send request to get changed config
         * send request to get detail config
         * set the executor parameter
         *
         * i literlly have no idea why they write this in such a complicated way
         */
        @Override
        public void run() {
            serverHealthCheck.isHealthStatus();
            List<CacheData> cacheDataList = new ArrayList<>();
            List<String> inInitializingCacheList = new ArrayList<>();
            cacheMap.forEach((key,val) -> cacheDataList.add(val));
            List<String> changedTpIds = checkUpdateDataIds(cacheDataList,inInitializingCacheList);
            for(String groupKey : changedTpIds) {
                String[] keys = groupKey.split("\\+");
                String tpId = keys[0];
                String itemId = keys[1];
                String namespace = keys[2];
                try {
                    String content = getServerConfig(namespace,itemId,tpId,3000L);
                    CacheData cacheData = cacheMap.get(tpId);
                    // String poolContent = ;
                } catch (Exception ignored) {
                    log.error("Failed to get the latest thread pool configuration.",ignored);
                }
            }
            for (CacheData cacheData : cacheDataList) {
                if (!cacheData.isInitializing() || inInitializingCacheList
                        .contains(GroupKey.getKeyTenant(cacheData.threadPoolId, cacheData.itemId, cacheData.tenantId))) {
                    cacheData.checkListenerMd5();
                    cacheData.setInitializing(false);
                }
            }
            inInitializingCacheList.clear();
            executorService.execute(this);
        }
    }

    /**
     * modify cacheData to formal url, pull request
     * @return
     */
    private List<String> checkUpdateDataIds(List<CacheData> cacheDataList, List<String> inInitializingCacheList) {
        StringBuilder sb = new StringBuilder();
        for(CacheData cacheData : cacheDataList) {
        }
        boolean isInitializingCacheList = !inInitializingCacheList.isEmpty();
        return checkUpdateTpids(sb.toString(),isInitializingCacheList);
    }
    private List<String> checkUpdateTpids(String probeUpdateString,boolean isInitializingCacheList) {
        if(StringUtils.isEmpty(probeUpdateString)) {
            return Collections.emptyList();
        }
        Map<String,String> params = new HashMap<>(2);
        params.put("Listening-Configs",probeUpdateString);
        params.put("Weight-Configs", UUID.randomUUID().toString());
        Map<String,String> headers = new HashMap<>(2);
        headers.put("Long-Pulling-Timeout","30000");
        headers.put("Client_Version","1.5");
        try {
            long readTimeoutMs = 30000 + Math.round(30000 >> 1);
            Result result = agent.httpPostByConfig("1",headers,params,readTimeoutMs);
            if(result != null) {
                return
            }
        } catch () {

        }
    }

    /**
     * parse the response of given response
     * @param response
     * @return
     */
    public List<String> parseUpdateDataIdResponse(String response) {
         if(ObjectUtils.isEmpty(response)) {
             return Collections.emptyList();
         }
         try {
             response = URLDecoder.decode(response);
         } catch (Exception e) {

         }
         List<String> updateList = new LinkedList<>();
         for(String dataIdAndGroup : response.split(String.valueOf((char) 2))){
             if(!ObjectUtils.isEmpty(dataIdAndGroup)) {
                 String[] keyArr = dataIdAndGroup.split("");
                 String dataId = keyArr[0];
                 String group = keyArr[1];
                 if(keyArr.length == 3) {
                     String tenant = keyArr[2];
                     updateList.add(GroupKey.getKeyTenant(dataId,group,tenant));
                 }
             }
         }
         return updateList;
    }

    public String getServerConfig(String namespace,String itemId,String threadPoolId,long readTimeout) {
        Map<String,String> params = new HashMap<>(3);
        params.put("namespace",namespace);
        params.put("itemId",itemId);
        params.put("tpId",itemId);
        params.put("instanceId",identify);
        Result result =
    }
}
