package com.example.service;

import java.util.Map;

public class ConfigServletInner {

    /**
     * Poll configuration.
     *
     * @param request          http servlet request
     * @param response         http servlet response
     * @param clientMd5Map     client md5 map
     * @param probeRequestSize probe request size
     * @return
     */
    public String doPollingConfig(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) {
        if (LongPollingService.isSupportLongPolling(request) && weightVerification(request)) {
            longPollingService.addLongPollingClient(request, response, clientMd5Map, probeRequestSize);
            return HttpServletResponse.SC_OK + "";
        }
        return HttpServletResponse.SC_OK + "";
    }

}
