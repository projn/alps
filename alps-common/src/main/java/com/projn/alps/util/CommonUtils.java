package com.projn.alps.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.projn.alps.define.CommonDefine.*;

/**
 * common utils
 *
 * @author : sunyuecheng
 */
public final class CommonUtils {

    /**
     * format exception info
     *
     * @param e :
     * @return String :
     */
    public static String formatExceptionInfo(Throwable e) {
        if (e == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();

        sb.append(e);
        sb.append("\nTrace:\n");

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement ele : trace) {
            sb.append("\t").append(ele).append("\n");
        }

        return sb.toString();
    }

    /**
     * get current day begin time
     *
     * @return long :
     */
    public static long getCurrentDayBeginTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * get current day end time
     *
     * @return long :
     */
    public static long getCurrentDayEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, DAY_HOURS);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

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
     * is ip address
     *
     * @param address :
     * @return boolean :
     */
    public static boolean isIpAddress(String address) {
        if (address == null || address.length() < MIN_IP_ADDRESS_LEN
                || address.length() > MAX_IP_ADDRESS_LEN || address.isEmpty()) {
            return false;
        }

        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])"
                + "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(rexp);
        Matcher matcher = pattern.matcher(address);

        return matcher.find();
    }

    /**
     * ip to long
     *
     * @param strIp :
     * @return long :
     */
    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);

        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    /**
     * int to mask
     *
     * @param bitMask :
     * @return String :
     */
    public static String intToMask(int bitMask) {
        if (bitMask > 32) {
            return null;
        }

        int[] tmpMask = {0, 0, 0, 0};
        int times = bitMask / 8;
        int i = 0;
        for (; i < times; i++) {
            tmpMask[i] = 255;
        }

        for (int j = 1; j <= 8; j++) {
            if (j <= bitMask - times * 8) {
                tmpMask[i] = 2 * tmpMask[i] + 1;
            } else {
                tmpMask[i] = 2 * tmpMask[i];
            }
        }

        return Integer.toString(tmpMask[0]) + "." + Integer.toString(tmpMask[1])
                + "." + Integer.toString(tmpMask[2]) + "." + Integer.toString(tmpMask[3]);
    }

    private CommonUtils() {
    }

}
