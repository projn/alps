package com.projn.alps.util;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * str utils
 *
 * @author : sunyuecheng
 */
public final class StrUtils {

    /**
     * is numeric
     *
     * @param str :
     * @return boolean :
     */
    public static boolean isNumeric(String str) {
        final String regex = "[0-9]*";

        if (!StringUtils.isEmpty(str)) {
            return str.matches(regex);
        }
        return false;
    }

    /**
     * is sample string
     *
     * @param str :
     * @return boolean :
     */
    public static boolean isSampleString(String str) {
        final String regex = "[,\\.\\x20a-zA-Z0-9_-]*";

        if (!StringUtils.isEmpty(str)) {
            return str.matches(regex);
        }
        return false;
    }

    /**
     * ip sort
     *
     * @param str     :
     * @param maxSize :
     * @return String :
     */
    public static String ipSort(String str, int maxSize) {
        String[] ips = str.split(",");

        Arrays.sort(ips);

        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (String ip : ips) {
            ip = ip.trim();
            if (ip.isEmpty()) {
                continue;
            }

            if (maxSize > 0) {
                int addSize = (first ? 0 : 1) + ip.length();
                if (sb.length() + addSize > maxSize) {
                    break;
                }
            }

            if (first) {
                first = false;
            } else {
                sb.append(",");
            }

            sb.append(ip);
        }

        return sb.toString();
    }

    /**
     * ip sort
     *
     * @param str :
     * @return String :
     */
    public static String ipSort(String str) {
        String[] ips = str.split(",");

        Arrays.sort(ips);

        StringBuilder sb = new StringBuilder();

        for (int index = 0; index < ips.length; index++) {
            if (index == ips.length - 1) {
                sb.append(ips[index].trim());
            } else {
                sb.append(ips[index].trim() + ",");
            }
        }

        return sb.toString();
    }

    private StrUtils() {
    }
}
