package com.example.util;

import com.example.entity.Constants;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

public class HttpUtil {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    /**
     * private method : return HttpURLConnection for the given url & method
     * @param url
     * @param method
     * @return
     * @throws Exception
     */
    private static HttpURLConnection createConnection(String url, String method) throws Exception {
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            return connection;
        } catch (Throwable var1) {
            throw var1;
        }
    }

    /**
     * add params to the url, nice code
     * @param url
     * @param queryParams
     * @return
     */
    @SneakyThrows
    public static String buildUrl(String url, Map<String,String> queryParams) {
        if(Objects.isNull(queryParams)) {
            return url;
        }
        boolean isFirst = true;
        StringBuilder builder = new StringBuilder(url);
        if(isFirst) {
            isFirst = false;
            builder.append("?");
        }
        for(Map.Entry<String,String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = URLEncoder.encode(queryParams.get(key), Constants.ENCODE)
                    .replaceAll("\\+","%20");
            if(key != null) {
                builder.append("&").append(key).append("=").append(value);
            }
        }
        return builder.toString();
    }
}
