package com.example.remote;

import com.example.entity.CacheData;
import com.example.entity.Constants;
import com.example.entity.Result;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.util.*;
import java.util.concurrent.*;

public class ClientWorker {
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
        /**
         *  launch the daemon thread to get the change information
         *  only execute once at the autoconfigure period, you know ?
         */
        executor.schedule(() -> {
            executorService.execute(new LongPollingRunnable(cacheMap.isEmpty(), cacheCondition));
        },1, TimeUnit.MILLISECONDS);
    }
    class LongPollingRunnable implements Runnable {
        private boolean cacheMapInitEmptyFlag;
        private final CountDownLatch cacheCondition;
        public LongPollingRunnable(boolean cacheMapInitEmptyFlag, CountDownLatch cacheCondition) {
            this.cacheMapInitEmptyFlag = cacheMapInitEmptyFlag;
            this.cacheCondition = cacheCondition;
        }

        @Override
        public void run() {
            serverHealthCheck.isHealthStatus();
            List<String> changedTpIds = checkUpdateDataIds();
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

}
