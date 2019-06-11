package com.projn.alps.third.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.FileInputStream;
import java.util.Properties;


public class KafkaTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTest.class);
    private static String filePath = null;

    public static void main(String[] args) {
        String path = KafkaTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContext ctx = new FileSystemXmlApplicationContext("file:" + filePath + "applicationContext.xml");

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(filePath + "config/context.properties"));

        } catch (Exception e) {
            LOGGER.error("Read server configure info error,error code(" + e.getMessage() + ")!");
            return;
        }

    }

}
