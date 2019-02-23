package com.projn.alps.util;

import org.junit.Test;

public class Base64UtilsTest {
    @Test
    public void encodeData() throws Exception {
        Base64Utils.encodeData("test".getBytes());
    }

    @Test
    public void decodeData() throws Exception {
    }

}