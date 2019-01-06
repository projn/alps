package com.projn.alps.util;

import org.apache.commons.codec.binary.Base64;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;

/**
 * base 64 utils
 *
 * @author : sunyuecheng
 */
public final class Base64Utils {
    /**
     * encode data
     *
     * @param data :
     * @return String :
     * @throws Exception :
     */
    public static String encodeData(byte[] data) throws Exception {
        if (data == null) {
            return null;
        }

        return new String(Base64.encodeBase64(data), DEFAULT_ENCODING);
    }

    /**
     * decode data
     *
     * @param data :
     * @return byte[] :
     */
    public static byte[] decodeData(byte[] data) {
        if (data == null) {
            return null;
        }

        return Base64.decodeBase64(data);
    }

    private Base64Utils() {
    }
}
