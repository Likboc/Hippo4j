package com.example.util;

import com.example.constant.Constants;
import com.example.constant.HttpMediaType;
import com.example.constant.HttpMethod;
import com.example.entity.http.HttpClientResponse;
import com.example.entity.http.JdkHttpClientResponse;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import com.example.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import static com.example.constant.HttpHeaderConstants.CONTENT_LENGTH;

/**
 * http util
 */
@Slf4j
public class HttpUtil {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    /**
     * Send a get network request.
     *
     * @param url     target url
     * @param headers headers
     * @param params  form data
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T get(String url, Map<String, String> headers, Map<String, String> params, long timeout, Class<T> clazz) {
        return execute(buildUrl(url, params), HttpMethod.GET, null, headers, timeout, clazz);
    }

    /**
     * Send a get network request.
     *
     * @param url    target url
     * @param params form data
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return execute(buildUrl(url, params), HttpMethod.GET, null, null);
    }

    /**
     * Send a get network request.
     *
     * @param url target url
     * @return
     */
    public static String get(String url) {
        return execute(url, HttpMethod.GET, null, null);
    }

    /**
     * Send a get network request.
     *
     * @param url   target url
     * @param clazz return the target data type
     * @param <T>   return the target data type
     * @return
     */
    public static <T> T get(String url, Class<T> clazz) {
        return JSONUtil.parseObject(get(url), clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url   target url
     * @param body  request body
     * @param clazz return the target data type
     * @param <T>   return the target data type
     * @return
     */
    public static <T> T post(String url, Object body, Class<T> clazz) {
        String result = post(url, body);
        return JSONUtil.parseObject(result, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param body    request body
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T post(String url, Object body, long timeout, Class<T> clazz) {
        String result = post(url, body, timeout);
        return JSONUtil.parseObject(result, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param headers headers
     * @param params  form data
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T post(String url, Map<String, String> headers, Map<String, String> params, long timeout, Class<T> clazz) {
        return execute(buildUrl(url, params), HttpMethod.POST, null, headers, timeout, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param headers headers
     * @param body    request body
     * @param timeout request timeout
     * @param clazz   return the target data type
     * @param <T>     return the target data type
     * @return
     */
    public static <T> T post(String url, Map<String, String> headers, Object body, long timeout, Class<T> clazz) {
        return execute(url, HttpMethod.POST, body, headers, timeout, clazz);
    }

    /**
     * Send a post network request.
     *
     * @param url  target url
     * @param body request body
     * @return
     */
    public static String post(String url, Object body) {
        return execute(url, HttpMethod.POST, body, null);
    }

    /**
     * Send a post network request.
     *
     * @param url     target url
     * @param body    request body
     * @param timeout request timeout
     * @return
     */
    public static String post(String url, Object body, long timeout) {
        return execute(url, HttpMethod.POST, body, null, timeout, String.class);
    }

    /**
     * Send a post network request.
     *
     * @param url  target url
     * @param json json data
     * @return
     */
    public static String postJson(String url, String json) {
        return executeJson(url, HttpMethod.POST, json, null);
    }

    /**
     * Send a put network request.
     *
     * @param url  target url
     * @param body request body
     * @return
     */
    public static String put(String url, Object body) {
        return execute(url, HttpMethod.PUT, body, null);
    }

    /**
     * Send a put network request.
     *
     * @param url     target url
     * @param body    request body
     * @param headers headers
     * @return
     */
    public static String put(String url, Object body, Map<String, String> headers) {
        return execute(url, HttpMethod.PUT, body, headers);
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

    private static String executeJson(String url, String method, String json, Map<String, String> headers) {
        // necessary to check is it's a json data
        return execute(url, method, json, headers);
    }

    private static String execute(String url,String method, Object body, Map<String,String> headers) {
        HttpURLConnection connection = createConnection(url,method);
        HttpClientResponse response = null;
        try {
            response = doExecute(connection, body, headers);
            return response.getBodyString();
        } finally {
            Optional.ofNullable(response).ifPresent(each -> each.close());
        }
    }

    private static <T> T execute(String url,String method, Object body, Map<String,String> headers, long timeout,Class<T> clazz) {
        HttpURLConnection connection = createConnection(url,method);
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

    /**
     * basic connection config of http
     * @param url
     * @param method
     * @return
     */
    @SneakyThrows
    private static HttpURLConnection createConnection(String url, String method) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setRequestMethod(method);
        connection.setRequestProperty(Constants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        return connection;
    }
    @SneakyThrows
    private static HttpURLConnection createConnection(String url, String method, long timeout) {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(Integer.parseInt(String.valueOf(timeout)));
        connection.setReadTimeout(Integer.parseInt(String.valueOf(timeout)));
        connection.setRequestMethod(method);
        connection.setRequestProperty(Constants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        return connection;
    }

    /**
     * add param & send http request
     * @param connection
     * @param body
     * @param headers
     * @return
     */
    @SneakyThrows
    private static HttpClientResponse doExecute(HttpURLConnection connection, Object body, Map<String, String> headers) {
        if(headers != null && !headers.isEmpty()) {
            for(Map.Entry<String,String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
        }
        // 优化点
        String bodyString = JSONUtil.toJSONString(body);
        if (!StringUtil.isEmpty(bodyString)) {
            connection.setDoOutput(true);
            byte[] b = bodyString.getBytes();
            connection.setRequestProperty(CONTENT_LENGTH, String.valueOf(b.length));
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(b, 0, b.length);
            outputStream.flush();
            IoUtil.closeQuietly(outputStream);
        }
        connection.connect();
        JdkHttpClientResponse response = new JdkHttpClientResponse(connection);
        return response;
    }
}
