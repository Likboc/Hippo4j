package com.example.util;

import org.springframework.util.StringUtils;

import static com.example.constant.Constants.GROUP_KEY_DELIMITER;

public class GroupKey {
    private static String doGetKey(String dataId, String group,String datumStr) {
         StringBuilder sb = new StringBuilder();
         urlEncode(dataId,sb);
         sb.append(GROUP_KEY_DELIMITER);
         urlEncode(group,sb);
         if(!StringUtils.isEmpty(datumStr)) {
             sb.append(GROUP_KEY_DELIMITER);
             urlEncode(datumStr,sb);
         }
         return sb.toString();
    }

    /**
     * used for url encode when carry some params
     * @param str
     * @param sb
     */
    public static void urlEncode(String str, StringBuilder sb) {
        for (int idx = 0; idx < str.length(); ++idx) {
            char c = str.charAt(idx);
            if ('+' == c) {
                sb.append("%2B");
            } else if ('%' == c) {
                sb.append("%25");
            } else {
                sb.append(c);
            }
        }
    }
}
