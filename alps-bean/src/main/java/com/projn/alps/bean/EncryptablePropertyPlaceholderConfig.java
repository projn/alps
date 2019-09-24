package com.projn.alps.bean;

import com.projn.alps.util.Base64Utils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.util.*;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;

/**
 * encryptable property placeholder config
 *
 * @author : sunyuecheng
 */
@Component
public class EncryptablePropertyPlaceholderConfig extends PropertyPlaceholderConfigurer {
    private static final String KEY_STR = EncryptablePropertyPlaceholderConfig.class.getName();

    private static final String KEY_ALGORITHM_DES = "DES";
    private static final String RANDOM_ALGORITHM_SHA1 = "SHA1PRNG";

    private static final String ENCRYPTED_PROPERTY_HEADER = "encrypt.";

    private static final String SCAN_PROPERTY_PATH_CLASSPATH = "classpath*:config/*.properties";
    private static final String SCAN_PROPERTY_PATH_FILE_FORMAT = "file:%s/config/*.properties";
    private static final String SCAN_PROPERTY_PATH_SPRING_CONFIG_LOCATION_FORMAT = "file:%s";

    /**
     * system property spring config location key
     */
    public static final String SYSTEM_PROPERTY_SPRING_CONFIG_LOCATION_KEY = "spring.config.location";
    private static final String SYSTEM_PROPERTY_CONFIG_DIR_KEY = "config.dir";

    private Key desKey;

    EncryptablePropertyPlaceholderConfig() throws Exception {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM_DES);
            SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_ALGORITHM_SHA1);
            secureRandom.setSeed(KEY_STR.getBytes());
            generator.init(secureRandom);
            desKey = generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String configDir = System.getProperty(SYSTEM_PROPERTY_CONFIG_DIR_KEY);
        String springConfigLocation = System.getProperty(SYSTEM_PROPERTY_SPRING_CONFIG_LOCATION_KEY);

        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver
                = new PathMatchingResourcePatternResolver();
        Resource[] resourceClass =
                pathMatchingResourcePatternResolver.getResources(SCAN_PROPERTY_PATH_CLASSPATH);
        Resource[] resourceFile =
                pathMatchingResourcePatternResolver.getResources(
                        String.format(SCAN_PROPERTY_PATH_FILE_FORMAT, configDir));
        Resource[] resourceSpringContext =
                pathMatchingResourcePatternResolver.getResources(
                        String.format(SCAN_PROPERTY_PATH_SPRING_CONFIG_LOCATION_FORMAT, springConfigLocation));

        List<Resource> resourceList = new ArrayList<>();
        resourceList.addAll(Arrays.asList(resourceClass));
        resourceList.addAll(Arrays.asList(resourceFile));
        resourceList.addAll(Arrays.asList(resourceSpringContext));
        this.setLocations(resourceList.toArray(new Resource[]{}));
    }

    /**
     * get encrypt string
     *
     * @param str :
     * @return String :
     */
    public String getEncryptString(String str) {
        try {
            byte[] bytes = str.getBytes(DEFAULT_ENCODING);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] doFinal = cipher.doFinal(bytes);
            return Base64Utils.encodeData(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get decrypt string
     *
     * @param str :
     * @return String :
     */
    public String getDecryptString(String str) {
        try {
            byte[] bytes = Base64Utils.decodeData(str.getBytes());
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            byte[] doFinal = cipher.doFinal(bytes);
            return new String(doFinal, DEFAULT_ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        try {
            Set<String> propertyNameSet = props.stringPropertyNames();
            for (String propertyName : propertyNameSet) {
                if (propertyName.startsWith(ENCRYPTED_PROPERTY_HEADER)) {
                    String newPropertyName = propertyName.substring(ENCRYPTED_PROPERTY_HEADER.length());

                    String encryptValue = props.getProperty(propertyName);
                    String decryptValue = getDecryptString(encryptValue);

                    props.setProperty(newPropertyName, decryptValue);
                }
            }
        } catch (Exception e) {
            throw new BeanInitializationException(e.getMessage());
        }
        super.processProperties(beanFactoryToProcess, props);
    }

}
