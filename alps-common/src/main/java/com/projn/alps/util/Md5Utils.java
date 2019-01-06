package com.projn.alps.util;

import java.security.MessageDigest;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;

/**
 * md 5 utils
 *
 * @author : sunyuecheng
 */
public final class Md5Utils {

    /**
     * md 5
     */
    private static final String MD5 = "MD5";

    /**
     * make md5
     *
     * @param data :
     * @return String :
     * @throws Exception :
     */
    public static String makeMd5(String data) throws Exception {
        if (data == null) {
            return null;
        }
        MessageDigest md = MessageDigest.getInstance(MD5);
        md.update(data.getBytes(DEFAULT_ENCODING));
        byte[] buffer = md.digest();

        int i;
        StringBuffer strBuf = new StringBuffer("");
        for (int offset = 0; offset < buffer.length; offset++) {
            i = buffer[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                strBuf.append("0");
            }
            strBuf.append(Integer.toHexString(i));
        }
        return strBuf.toString();
    }

    private Md5Utils() {
    }
}
