package com.example.service;

import java.util.List;
import java.util.Map;

public class LongPollingService {

    public void addLongPollingClient(HttpServletRequest req, HttpServletResponse rsp, Map<String, String> clientMd5Map,
                                     int probeRequestSize) {
        String str = req.getHeader(LONG_POLLING_HEADER);
        String noHangUpFlag = req.getHeader(LONG_POLLING_NO_HANG_UP_HEADER);
        int delayTime = SwitchService.getSwitchInteger(SwitchService.FIXED_DELAY_TIME, 500);
        long timeout = Math.max(10000, Long.parseLong(str) - delayTime);
        if (isFixedPolling()) {
            timeout = Math.max(10000, getFixedPollingInterval());
        } else {
            List<String> changedGroups = Md5ConfigUtil.compareMd5(req, clientMd5Map);
            if (!changedGroups.isEmpty()) {
                generateResponse(rsp, changedGroups);
                return;
            } else if (noHangUpFlag != null && noHangUpFlag.equalsIgnoreCase(TRUE_STR)) {
                log.info("New initializing cacheData added in.");
                return;
            }
        }
        String clientIdentify = RequestUtil.getClientIdentify(req);
        final AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0L);
        ConfigExecutor.executeLongPolling(new ClientLongPolling(asyncContext, clientMd5Map, clientIdentify, probeRequestSize,
                timeout - delayTime, Pair.of(req.getHeader(CLIENT_APP_NAME_HEADER), req.getHeader(CLIENT_VERSION))));
    }
}
