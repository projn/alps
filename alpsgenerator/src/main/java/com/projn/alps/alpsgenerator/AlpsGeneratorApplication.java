package com.projn.alps.alpsgenerator;

import com.projn.alps.alpsgenerator.tool.GeneratorTools;
import org.springframework.util.StringUtils;

import java.io.File;

import static java.lang.System.exit;

/**
 * alps generator application
 *
 * @author : sunyuecheng
 */
public class AlpsGeneratorApplication {

    /**
     * main
     *
     * @param args :
     */
    public static void main(String[] args) {
//        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
//        JoranConfigurator configurator = new JoranConfigurator();
//        configurator.setContext(loggerContext);
//        loggerContext.reset();
//        try {
//            configurator.doConfigure("D:\\alps\\AlpsModuleGenerator\\src\\main\\resources\\logback-config.xml");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String generatorConfigPath = null;
        if(args == null || args.length!=1 || StringUtils.isEmpty(args[0])) {
            generatorConfigPath = System.getProperty("user.dir") + File.separator + "generator_config.xml";
        } else {
            generatorConfigPath = args[0];
        }

        GeneratorTools generatorTools = new GeneratorTools();
        generatorTools.loadConfig(generatorConfigPath);

        exit(0);
    }
}
