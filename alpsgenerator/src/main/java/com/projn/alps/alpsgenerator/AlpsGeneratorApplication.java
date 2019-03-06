package com.projn.alps.alpsgenerator;

import com.projn.alps.alpsgenerator.tool.GeneratorTools;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.commons.lang3.StringUtils;

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

        Configurator.initialize(null, "classpath:log4j2-config.xml");

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
