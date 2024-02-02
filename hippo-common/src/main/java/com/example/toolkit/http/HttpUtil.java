package com.example.toolkit.http;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

public class HttpUtil {
    private static <T> T execute(String url, String method, Object body, Map<String, String> headers, long timeout, Class<T> clazz) {
        HttpURLConnection connection = createConnection(url, method, timeout);
        HttpClientResponse response = null;
        try {
            response = doExecute(connection, body, headers);
            if (clazz == String.class) {
                return (T) response.getBodyString();
            }
            return JSONUtil.parseObject(response.getBodyString(), clazz);
        } finally {
            Optional.ofNullable(response).ifPresent(each -> each.close());
        }
    }
}
