package com.sct.mongodbtest;

import java.io.File;
import java.net.URLDecoder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoDbTest
{    
    private static final Logger logger = Logger.getLogger(MongoDbTest.class);
    private static String filePath = null;
    
    public static void main(String[] args) {
        String path = MongoDbTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {  
            path = URLDecoder.decode(path, "UTF-8");
        }  
        catch (java.io.UnsupportedEncodingException e) {  
            return;  
        }  
        File file = new File(path);  
        filePath = file.getParent() + "/";
        
        String configPath = filePath + "config/log4j.properties";
        try {
            configPath = URLDecoder.decode(configPath, "UTF-8");        
            PropertyConfigurator.configure(configPath);
        } catch (Exception e) {
            return;
        }
        
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ApplicationContext ctx = new FileSystemXmlApplicationContext("file:"+ filePath +"applicationContext.xml");

    }
}
