package com.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final String EMPTY = "";

    public static final char UNDERLINE = '_';

    public static final String[] EMPTY_ARRAY = new String[0];

    public static String emptyToNull(String str) {
        return (str == null || str.isEmpty()) ? null : str;
    }

    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    public static boolean isBlank(CharSequence str) {
        if ((str == null)) {
            return true;
        }
        int length = str.length();
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            boolean charNotBlank = Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a';
            if (!charNotBlank) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    public static boolean isAllNotEmpty(CharSequence... args) {
        return !hasEmpty(args);
    }

    public static boolean hasEmpty(CharSequence... strList) {
        if (ArrayUtil.isEmpty(strList)) {
            return true;
        }

        for (CharSequence str : strList) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, UNDERLINE);
    }

    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
            if (Character.isUpperCase(c)) {
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;
                if (null != preChar && Character.isUpperCase(preChar)) {
                    sb.append(c);
                } else if (null != nextChar && Character.isUpperCase(nextChar)) {
                    if (null != preChar && symbol != preChar) {
                        sb.append(symbol);
                    }
                    sb.append(c);
                } else {
                    if (null != preChar && symbol != preChar) {
                        sb.append(symbol);
                    }
                    sb.append(Character.toLowerCase(c));
                }
            } else {
                if (sb.length() > 0 && Character.isUpperCase(sb.charAt(sb.length() - 1)) && symbol != c) {
                    sb.append(symbol);
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toCamelCase(CharSequence str, char symbol) {
        if (null == str || str.length() == 0) {
            return null;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        boolean upperCase = false;
        for (int i = 0; i < length; ++i) {
            char c = str.charAt(i);
            if (c == symbol) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String newBuilder(CharSequence... charSequences) {
        if (charSequences == null || charSequences.length == 0) {
            return StringUtil.EMPTY;
        }
        return createBuilder(charSequences).toString();
    }

    public static StringBuilder createBuilder(CharSequence... charSequences) {
        StringBuilder builder = new StringBuilder();
        if (charSequences == null || charSequences.length == 0) {
            return builder;
        }
        for (CharSequence sequence : charSequences) {
            builder.append(sequence);
        }
        return builder;
    }

    public static StringBuilder appends(StringBuilder builder, CharSequence... charSequences) {
        if (builder == null) {
            return createBuilder(charSequences);
        }
        if (charSequences == null || charSequences.length == 0) {
            return builder;
        }
        for (CharSequence sequence : charSequences) {
            builder.append(sequence);
        }
        return builder;
    }

    public static String replace(String str, String searchStr, String replaceStr) {
        return Pattern
                .compile(searchStr, Pattern.LITERAL)
                .matcher(str)
                .replaceAll(Matcher.quoteReplacement(replaceStr));
    }

    /**
     * <p>Splits the provided text into an array, separators specified.
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters,
     * @return an array of parsed Strings
     */
    public static String[] split(final String str, final String separatorChars) {
        if (str == null) {
            return null;
        }
        if (isBlank(str)) {
            return EMPTY_ARRAY;
        }
        if (isBlank(separatorChars)) {
            return str.split(" ");
        }
        return str.split(separatorChars);
    }

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param str    this str
     * @param prefix the suffix
     * @return Whether the prefix exists
     */
    public static boolean startWith(String str, String prefix) {
        if (isEmpty(str)) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * get the string before the delimiter
     *
     * @param str    string
     * @param symbol separator
     * @return String
     */
    public static String subBefore(String str, String symbol) {
        if (isEmpty(str) || symbol == null) {
            return str;
        }
        if (symbol.isEmpty()) {
            return EMPTY;
        }
        int pos = str.indexOf(symbol);
        if (-1 == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }
}
