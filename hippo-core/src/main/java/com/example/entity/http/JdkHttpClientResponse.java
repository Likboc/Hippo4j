package com.example.entity.http;

import com.example.constant.Constants;
import com.example.constant.HttpHeaderConstants;
import com.example.util.IoUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class JdkHttpClientResponse implements HttpClientResponse{
    private HttpURLConnection connection;

    private InputStream responseStream;

    private Header responseHeader;

    private static final String CONTENT_ENCODING = "gzip";

    public JdkHttpClientResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public Header getHeaders() {
        if (this.responseHeader == null) {
            this.responseHeader = Header.newInstance();
        }
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            this.responseHeader.addOriginalResponseHeader(entry.getKey(), entry.getValue());
        }
        return this.responseHeader;
    }

    @Override
    @SneakyThrows
    public InputStream getBody() {
        Header headers = getHeaders();
        InputStream errorStream = this.connection.getErrorStream();
        this.responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
        String contentEncoding = headers.getValue(HttpHeaderConstants.CONTENT_ENCODING);
        // Used to process http content_encoding, when content_encoding is GZIP, use GZIPInputStream
        if (CONTENT_ENCODING.equals(contentEncoding)) {
            byte[] bytes = IoUtil.tryDecompress(this.responseStream);
            return new ByteArrayInputStream(bytes);
        }
        return this.responseStream;
    }

    @Override
    public String getBodyString() {
        return IoUtil.toString(this.getBody(), Constants.ENCODE);
    }

    @SneakyThrows
    @Override
    public int getStatusCode() {
        return connection.getResponseCode();
    }

    @SneakyThrows
    @Override
    public String getStatusText() {
        return connection.getResponseMessage();
    }

    @Override
    public void close() {
        IoUtil.closeQuietly(this.responseStream);
    }
}
