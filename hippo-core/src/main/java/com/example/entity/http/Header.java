package com.example.entity.http;

import com.example.constant.Constants;
import com.example.constant.HttpHeaderConstants;
import com.example.constant.HttpMediaType;
import com.example.util.StringUtil;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;

public class Header {

    public static final Header EMPTY = Header.newInstance();

    private final Map<String, String> header;

    private final Map<String, List<String>> originalResponseHeader;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String DEFAULT_ENCODING = "gzip";

    private Header() {
        header = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        originalResponseHeader = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        addParam(HttpHeaderConstants.CONTENT_TYPE, HttpMediaType.APPLICATION_JSON);
        addParam(HttpHeaderConstants.ACCEPT_CHARSET, DEFAULT_CHARSET);
    }

    public static Header newInstance() {
        return new Header();
    }

    /**
     * Add the key and value to the header.
     *
     * @param key   the key
     * @param value the value
     * @return header
     */
    public Header addParam(String key, String value) {
        if (StringUtil.isNotEmpty(key)) {
            header.put(key, value);
        }
        return this;
    }

    public Header setContentType(String contentType) {
        if (contentType == null) {
            contentType = HttpMediaType.APPLICATION_JSON;
        }
        return addParam(HttpHeaderConstants.CONTENT_TYPE, contentType);
    }

    public Header build() {
        return this;
    }

    public String getValue(String key) {
        return header.get(key);
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        return header.entrySet().iterator();
    }

    /**
     * Transfer to KV part list. The odd index is key and the even index is value.
     *
     * @return KV string list
     */
    public List<String> toList() {
        List<String> list = new ArrayList<>(header.size() * 2);
        Iterator<Map.Entry<String, String>> iterator = iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }

    /**
     * set original format response header.
     *
     * <p>Currently only corresponds to the response header of JDK.
     *
     * @param key    original response header key
     * @param values original response header values
     */
    public void addOriginalResponseHeader(String key, List<String> values) {
        if (StringUtil.isNotEmpty(key)) {
            this.originalResponseHeader.put(key, values);
            addParam(key, values.get(0));
        }
    }

    public void clear() {
        header.clear();
        originalResponseHeader.clear();
    }

    private String analysisCharset(String contentType) {
        String[] values = contentType.split(";");
        String charset = Constants.ENCODE;
        if (values.length == 0) {
            return charset;
        }
        for (String value : values) {
            if (value.startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }
        return charset;
    }
    @Override
    public String toString() {
        return "Header{" + "headerToMap=" + header + '}';
    }
}
