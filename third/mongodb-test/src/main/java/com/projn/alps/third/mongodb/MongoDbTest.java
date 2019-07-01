package com.projn.alps.third.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.net.URLDecoder;

public class MongoDbTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbTest.class);
    private static String filePath = null;

    public static void main(String[] args) {
        String path = MongoDbTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return;
        }
        File file = new File(path);
        filePath = file.getParent() + "/";

        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContext ctx = new FileSystemXmlApplicationContext("file:" + filePath + "applicationContext.xml");

    }
}
