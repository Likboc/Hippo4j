package com.example.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
    private static HttpURLConnection createConnection(String url, String method) throws Exception {
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            return connection;
        } catch (Throwable var1) {
            throw var1;
        }
    }
}
