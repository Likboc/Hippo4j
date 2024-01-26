package com.example.entity;

public class Constants {
    /**
     * thread pool detail
     */
    public static final String TP_ID = "tpId";
    public static final String ITEM_ID = "itemId";
    public static final String NAMESPACE = "namespace";
    public static final String GROUP_KEY = "groupkey";
    public static final int CONFIG_LONG_POLL_TIMEOUT = 30000;
    public static final String LINE_SEPARATOR = Character.toString((char) 1);
    public static final String WORD_SEPARATOR = Character.toString((char) 2);
    // available processors number
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final int HTTP_EXECUTE_TIMEOUT = 5000;

}
