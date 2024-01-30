package com.example.util;

import com.example.constant.Constants;
import lombok.SneakyThrows;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class IoUtil {

    public static byte[] tryDecompress(InputStream raw) throws IOException {
        try (
                GZIPInputStream gis = new GZIPInputStream(raw);
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            copy(gis, out);
            return out.toByteArray();
        }
    }

    @SneakyThrows
    public static String toString(InputStream input, String encoding) {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        return (null == encoding) ? toString(new InputStreamReader(input, Constants.ENCODE))
                : toString(new InputStreamReader(input, encoding));
    }

    public static String toString(Reader reader) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(reader, sw);
        return sw.toString();
    }

    public static long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[1 << 12];
        long count = 0;
        for (int n = 0; (n = input.read(buffer)) >= 0;) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        int totalBytes = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);

            totalBytes += bytesRead;
        }

        return totalBytes;
    }
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {
        }
    }
}
