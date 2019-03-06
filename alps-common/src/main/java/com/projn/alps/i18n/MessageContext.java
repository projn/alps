package com.projn.alps.i18n;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.*;

import static com.projn.alps.define.CommonDefine.COLLECTION_INIT_SIZE;

/**
 * message context
 *
 * @author : sunyuecheng
 */
public final class MessageContext {
    private static Logger logger = LoggerFactory.getLogger(MessageContext.class);

    private static final Locale DEFAILT_LOCALE = Locale.CHINA;

    private static final String RESOURCEBUNDLE_DEFAULT_DECODING = "ISO-8859-1";

    private static final String PROPERTIES_FILE_ENCODING = "UTF-8";

    private static final int PROPERTIES_FILE_NAME_PART_LEN = 3;

    private static final int MODULE_NAME_INDEX = 0;

    private static final int LANG_INDEX = 1;

    private static final int COUNTRY_INDEX = 2;

    private static Map<String, Map<Locale, Map<String, String>>> moduleLocalKeyMessageMap =
            new HashMap<String, Map<Locale, Map<String, String>>>();

    /**
     * get message
     *
     * @param moduleName        :
     * @param locale            :
     * @param messageKey        :
     * @param messageParameters :
     * @return String :
     */
    public static String getMessage(String moduleName, Locale locale, String messageKey, Object... messageParameters) {
        Locale tmpLocale = locale == null ? DEFAILT_LOCALE : locale;

        if (moduleLocalKeyMessageMap.containsKey(moduleName)
                && moduleLocalKeyMessageMap.get(moduleName) != null) {

            Map<Locale, Map<String, String>> localKeyMessageMap = moduleLocalKeyMessageMap.get(moduleName);

            if (localKeyMessageMap.containsKey(tmpLocale) && localKeyMessageMap.get(tmpLocale) != null) {

                Map<String, String> keyMessageMap = localKeyMessageMap.get(tmpLocale);
                if (keyMessageMap.containsKey(messageKey)) {
                    String messageValue = keyMessageMap.get(messageKey);
                    if (StringUtils.isEmpty(messageValue)) {
                        return null;
                    }
                    return MessageFormat.format(messageValue, messageParameters);
                }
            }
        }
        return null;
    }

    /**
     * load module local key message
     *
     * @param fileDir :
     * @return boolean :
     */
    public static boolean loadModuleLocalKeyMessage(String fileDir) {
        if (StringUtils.isEmpty(fileDir)) {
            logger.error("Invaild param.");
            return false;
        }

        File file = new File(fileDir);
        File[] fileList = file.listFiles();
        if (fileList == null) {
            logger.error("Invaild file dir, file dir({}).", fileDir);
            return true;
        }
        for (File subFile : fileList) {
            if (subFile.isDirectory() || subFile.isHidden()
                    || !subFile.getName().endsWith(".properties") || !subFile.getName().contains("_")) {
                continue;
            }

            String fileName = subFile.getName();
            String[] subNameList = fileName.split("_");
            if (subNameList.length != PROPERTIES_FILE_NAME_PART_LEN || subNameList[COUNTRY_INDEX].indexOf(".") == -1) {
                continue;
            }
            String moduleName = subNameList[MODULE_NAME_INDEX];
            String lang = subNameList[LANG_INDEX];
            String country = subNameList[COUNTRY_INDEX].substring(0, subNameList[COUNTRY_INDEX].indexOf("."));
            Locale locale = new Locale(lang, country);

            ResourceBundle resourceBundle = null;
            BufferedInputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(subFile.getAbsolutePath()));
                resourceBundle = new PropertyResourceBundle(inputStream);
                inputStream.close();

                Enumeration<String> keys = resourceBundle.getKeys();
                while (keys.hasMoreElements()) {
                    String messageKey = keys.nextElement();
                    String messageValue =
                            new String(resourceBundle.getString(messageKey).getBytes(RESOURCEBUNDLE_DEFAULT_DECODING),
                                    Charset.forName(PROPERTIES_FILE_ENCODING));

                    if (moduleLocalKeyMessageMap.containsKey(moduleName)
                            && moduleLocalKeyMessageMap.get(moduleName) != null) {

                        Map<Locale, Map<String, String>> localKeyMessageMap = moduleLocalKeyMessageMap.get(moduleName);

                        if (localKeyMessageMap.containsKey(locale) && localKeyMessageMap.get(locale) != null) {
                            Map<String, String> keyMessageMap = localKeyMessageMap.get(locale);
                            keyMessageMap.put(messageKey, messageValue);
                        } else {
                            localKeyMessageMap.put(locale, new HashMap<String, String>(COLLECTION_INIT_SIZE));
                            Map<String, String> keyMessageMap = localKeyMessageMap.get(locale);
                            keyMessageMap.put(messageKey, messageValue);
                        }
                    } else {
                        moduleLocalKeyMessageMap.put(moduleName,
                                new HashMap<Locale, Map<String, String>>(COLLECTION_INIT_SIZE));
                        Map<Locale, Map<String, String>> localKeyMessageMap = moduleLocalKeyMessageMap.get(moduleName);
                        localKeyMessageMap.put(locale, new HashMap<String, String>(COLLECTION_INIT_SIZE));
                        Map<String, String> keyMessageMap = localKeyMessageMap.get(locale);
                        keyMessageMap.put(messageKey, messageValue);
                    }
                }

            } catch (RuntimeException e){
                logger.error("Load module local key message error, error info({}).", e.getMessage());
                return false;
            } catch(Exception e) {
                logger.error("Load module local key message error, error info({}).", e.getMessage());
                return false;
            }
        }
        return true;
    }

    private MessageContext() {
    }
}