package com.projn.alps.util;

import com.alibaba.fastjson.JSON;
import com.projn.alps.msg.filter.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * param check utils
 *
 * @author : sunyuecheng
 */
public final class ParamCheckUtils {

    /**
     * check param
     *
     * @param param :
     * @return boolean :
     * @throws Exception :
     */
    public static boolean checkParam(Object param) throws Exception {
        Class clazz = param.getClass();
        if (clazz.isAnnotationPresent(ParamLimit.class)) {
            ParamFilter paramFilter = (ParamFilter) clazz.getAnnotation(ParamFilter.class);
            if (paramFilter != null) {
                IParamFilter paramFilterClass = (IParamFilter) paramFilter.value().newInstance();
                if (!paramFilterClass.checkParam(param)) {
                    throw new Exception("User define param filter check error.");
                }
            }
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (StringUtils.isEmpty(fieldName)) {
                continue;
            }

            PropertyDescriptor pd = field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)
                    ? new PropertyDescriptor(fieldName, clazz)
                    : new PropertyDescriptor(fieldName, clazz, getGetterMethodName(fieldName), null);
            Method getMethod = pd.getReadMethod();

            Object fieldValue = getMethod.invoke(param, new Object[]{});
            Class type = field.getType();

            if (field.isAnnotationPresent(ParamLocation.class)) {
                ParamLocation paramLocation = (ParamLocation) field.getAnnotation(ParamLocation.class);
                if (paramLocation != null && paramLocation.location() == ParamLocationType.BODY) {
                    checkParam(fieldValue);
                }
                continue;
            }

            if (!field.isAnnotationPresent(ParamLimit.class)) {
                continue;
            }
            ParamLimit paramLimit = (ParamLimit) field.getAnnotation(ParamLimit.class);
            if (paramLimit == null) {
                continue;
            }

            if (!paramLimit.nullable()) {
                if (fieldValue == null) {
                    throw new Exception("Field is null, field name(" + fieldName + "),type(" + type.getName() + ").");
                }
            }
            if (fieldValue == null) {
                continue;
            }

            checkByType(fieldValue, paramLimit, fieldName, type);
        }
        return true;
    }

    private static String getGetterMethodName(String name) {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        sb.insert(0, "get");

        return sb.toString();
    }

    private static void checkByType(Object fieldValue, ParamLimit paramLimit, String fieldName, Class type)
            throws Exception {
        List<String> valueList = null;
        if (paramLimit.value().length != 0) {
            valueList = Arrays.asList(paramLimit.value());
        }

        if (Integer.class.equals(type) || int.class.equals(type)) {
            checkInt(fieldValue, paramLimit, fieldName, type, valueList);
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            checkLong(fieldValue, paramLimit, fieldName, type, valueList);
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            Boolean boolVal = (Boolean) fieldValue;
        } else if (Date.class.equals(type)) {
            Date date = (Date) fieldValue;
        } else if (Short.class.equals(type) || short.class.equals(type)) {
            checkShort(fieldValue, paramLimit, fieldName, type, valueList);
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            checkFloat(fieldValue, paramLimit, fieldName, type, valueList);
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            checkDouble(fieldValue, paramLimit, fieldName, type, valueList);
        } else if (String.class.equals(type)) {
            checkString(fieldValue, paramLimit, fieldName, type, valueList);
        } else if (List.class.equals(type)) {
            List<Object> objList = (List<Object>) fieldValue;
            for (Object obj : objList) {
                try {
                    ParamCheckUtils.checkParam(obj);
                } catch (Exception e) {
                    throw e;
                }
            }
        } else if (MultipartFile.class.equals(type)) {
        } else {
            throw new Exception("Unsupport value type,field name(" + fieldName + "),type(" + type.getName() + ").");
        }
    }

    private static void checkInt(Object fieldValue, ParamLimit paramLimit, String fieldName,
                                 Class type, List<String> valueList) throws Exception {
        Integer integerVal = (Integer) fieldValue;
        if (!"".equals(paramLimit.maxValue())) {
            Integer integerMaxVal = Integer.valueOf(paramLimit.maxValue());
            if (integerVal > integerMaxVal) {
                throw new Exception("Field is larger than " + integerMaxVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + integerVal + ").");
            }
        }
        if (!"".equals(paramLimit.minValue())) {
            Integer integerMinVal = Integer.valueOf(paramLimit.minValue());
            if (integerVal < integerMinVal) {
                throw new Exception("Field is smaller than " + integerMinVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + integerVal + ").");
            }
        }

        if (valueList != null) {
            if (!valueList.contains(integerVal.toString())) {
                throw new Exception("Field is invaild value, field name("
                        + fieldName + "),type(" + type.getName()
                        + "), value(" + integerVal + ").");
            }
        }
    }

    private static void checkLong(Object fieldValue, ParamLimit paramLimit, String fieldName,
                                  Class type, List<String> valueList) throws Exception {
        Long longVal = (Long) fieldValue;
        if (!"".equals(paramLimit.maxValue())) {
            Long longMaxVal = Long.valueOf(paramLimit.maxValue());
            if (longVal > longMaxVal) {
                throw new Exception("Field is larger than " + longMaxVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + longVal + ").");
            }
        }
        if (!"".equals(paramLimit.minValue())) {
            Long longMinVal = Long.valueOf(paramLimit.minValue());
            if (longVal < longMinVal) {
                throw new Exception("Field is smaller than " + longMinVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + longVal + ").");
            }
        }

        if (valueList != null) {
            if (!valueList.contains(longVal.toString())) {
                throw new Exception("Field is invaild value, field name("
                        + fieldName + "),type(" + type.getName()
                        + "), value(" + longVal + ").");
            }
        }
    }

    private static void checkShort(Object fieldValue, ParamLimit paramLimit, String fieldName,
                                   Class type, List<String> valueList) throws Exception {
        Short shortVal = (Short) fieldValue;
        if (!"".equals(paramLimit.maxValue())) {
            Short shortMaxVal = Short.valueOf(paramLimit.maxValue());
            if (shortVal > shortMaxVal) {
                throw new Exception("Field is larger than " + shortMaxVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + shortVal + ").");
            }
        }
        if (!"".equals(paramLimit.minValue())) {
            Short shortMinVal = Short.valueOf(paramLimit.minValue());
            if (shortVal < shortMinVal) {
                throw new Exception("Field is smaller than " + shortMinVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + shortVal + ").");
            }
        }

        if (valueList != null) {
            if (!valueList.contains(shortVal.toString())) {
                throw new Exception("Field is invaild value, field name("
                        + fieldName + "),type(" + type.getName()
                        + "), value(" + shortVal + ").");
            }
        }
    }

    private static void checkFloat(Object fieldValue, ParamLimit paramLimit, String fieldName,
                                   Class type, List<String> valueList) throws Exception {
        Float floatVal = (Float) fieldValue;
        if (!"".equals(paramLimit.maxValue())) {
            Float floatMaxVal = Float.valueOf(paramLimit.maxValue());
            if (floatVal > floatMaxVal) {
                throw new Exception("Field is larger than " + floatMaxVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + floatVal + ").");
            }
        }
        if (!"".equals(paramLimit.minValue())) {
            Float floatMinVal = Float.valueOf(paramLimit.minValue());
            if (floatVal < floatMinVal) {
                throw new Exception("Field is smaller than " + floatMinVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + floatVal + ").");
            }
        }
        if (paramLimit.precision() != 0) {
            String floatValStr = floatVal.toString();
            int pos = floatValStr.indexOf('.') + 1;
            if (floatValStr.length() - pos != paramLimit.precision()) {
                throw new Exception("Field precision do not equal with " + paramLimit.precision()
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + floatVal + ").");
            }
        }

        if (valueList != null) {
            if (!valueList.contains(floatVal.toString())) {
                throw new Exception("Field is invaild value, field name("
                        + fieldName + "),type(" + type.getName()
                        + "), value(" + floatVal + ").");
            }
        }
    }

    private static void checkDouble(Object fieldValue, ParamLimit paramLimit, String fieldName,
                                    Class type, List<String> valueList) throws Exception {
        Double doubleVal = (Double) fieldValue;
        if (!"".equals(paramLimit.maxValue())) {
            Double doubleMaxVal = Double.valueOf(paramLimit.maxValue());
            if (doubleVal > doubleMaxVal) {
                throw new Exception("Field is larger than " + doubleMaxVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + doubleVal + ").");
            }
        }
        if (!"".equals(paramLimit.minValue())) {
            Double doubleMinVal = Double.valueOf(paramLimit.minValue());
            if (doubleVal < doubleMinVal) {
                throw new Exception("Field is smaller than " + doubleMinVal
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + doubleVal + ").");
            }
        }
        if (paramLimit.precision() != 0) {
            String doubleValStr = doubleVal.toString();
            int pos = doubleValStr.indexOf('.') + 1;
            if (doubleValStr.length() - pos != paramLimit.precision()) {
                throw new Exception("Field precision do not equal with " + paramLimit.precision()
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + doubleVal + ").");
            }
        }

        if (valueList != null) {
            if (!valueList.contains(doubleVal.toString())) {
                throw new Exception("Field is invaild value, field name("
                        + fieldName + "),type(" + type.getName()
                        + "), value(" + doubleVal + ").");
            }
        }
    }

    private static void checkString(Object fieldValue, ParamLimit paramLimit, String fieldName,
                                    Class type, List<String> valueList) throws Exception {
        String strVal = (String) fieldValue;
        if (paramLimit.maxLength() != -1) {
            if (strVal.length() > paramLimit.maxLength()) {
                throw new Exception("Field length is larger than " + paramLimit.maxLength()
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + strVal + ").");
            }
        }
        if (paramLimit.minLength() != -1) {
            if (strVal.length() < paramLimit.minLength()) {
                throw new Exception("Field length is larger than " + paramLimit.minLength()
                        + ", field name(" + fieldName + "),type(" + type.getName()
                        + "), value(" + strVal + ").");
            }
        }

        if (valueList != null) {
            if (!valueList.contains(strVal)) {
                throw new Exception("Field is invaild value, field name("
                        + fieldName + "),type(" + type.getName()
                        + "), value(" + strVal + ").");
            }
        }

        if (paramLimit.type() != ParamCheckType.NULL) {
            switch (paramLimit.type()) {
                case JSON:
                    if (!ParamCheckUtils.isValidJson(strVal)) {
                        throw new Exception("Field is not json, field name(" + fieldName
                                + "),type(" + type.getName() + "), value(" + strVal + ").");
                    }
                    break;
                case IP_ADDRESS:
                    if (!ParamCheckUtils.isValidIpAddress(strVal)) {
                        throw new Exception("Field is not ip, field name(" + fieldName
                                + "),type(" + type.getName() + "), value(" + strVal + ").");
                    }
                    break;
                case PATH:
                    if (!ParamCheckUtils.isValidPath(strVal)) {
                        throw new Exception("Field is not path, field name(" + fieldName
                                + "),type(" + type.getName() + "), value(" + strVal + ").");
                    }
                    break;
                case REGEX:
                    if (!StringUtils.isEmpty(paramLimit.regex())) {
                        if (!strVal.matches(paramLimit.regex())) {
                            throw new Exception("Field is not match regex, field name(" + fieldName
                                    + "),type(" + type.getName() + "), value(" + strVal + ").");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * is valid ip address
     *
     * @param ip :
     * @return boolean :
     */
    public static boolean isValidIpAddress(String ip) {
        final String regex =
                "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\."
                        + "(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\"
                        + "d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";

        return ip.matches(regex);
    }

    /**
     * is valid path
     *
     * @param path :
     * @return boolean :
     */
    public static boolean isValidPath(String path) {
        final String regex = "[a-zA-Z]:(\\\\([0-9a-zA-Z]+))+|(\\/([0-9a-zA-Z]+))+";
        return path.matches(regex);
    }

    /**
     * is valid json
     *
     * @param json :
     * @return boolean :
     */
    public static boolean isValidJson(String json) {
        try {
            JSON.parse(json);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private ParamCheckUtils() {
    }
}
