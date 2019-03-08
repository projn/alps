package com.projn.alps.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.projn.alps.define.HttpDefine;
import com.projn.alps.msg.filter.ParamLocation;
import com.projn.alps.msg.filter.ParamLocationType;
import com.projn.alps.struct.HttpRequestInfo;
import com.projn.alps.struct.MsgRequestInfo;
import com.projn.alps.struct.WsRequestInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.projn.alps.define.CommonDefine.LANG_EN_US;
import static com.projn.alps.define.CommonDefine.RADIX_10;

/**
 * http param utils
 *
 * @author : sunyuecheng
 */
public final class RequestInfoUtils {

    /**
     * convert http request info
     *
     * @param request      :
     * @param pathParamMap :
     * @param bodyJson     :
     * @param file         :
     * @param clazz        :
     * @return HttpRequestInfo :
     * @throws Exception :
     */
    public static HttpRequestInfo convertHttpRequestInfo(HttpServletRequest request,
                                                         Map<String, String> pathParamMap,
                                                         JSONObject bodyJson,
                                                         MultipartFile file,
                                                         final Class<?> clazz) throws Exception {
        if (request == null || clazz == null) {
            throw new Exception("Invaild param.");
        }

        Field[] fields = clazz.getDeclaredFields();
        Object paramObj = clazz.newInstance();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (StringUtils.isEmpty(fieldName)) {
                throw new Exception("Invaild field name,field name(" + fieldName + ").");
            }
            String methodName = fieldName;
            if (field.isAnnotationPresent(JSONField.class)) {
                JSONField jsonField = (JSONField) field.getAnnotation(JSONField.class);
                String jsonFieldName = jsonField.name();
                if (!StringUtils.isEmpty(jsonFieldName)) {
                    fieldName = jsonFieldName;
                }
            }

            if (!field.isAnnotationPresent(ParamLocation.class)
                    || field.getAnnotation(ParamLocation.class) == null) {
                throw new Exception("Invaild param location,field name(" + fieldName + ").");
            }
            ParamLocation paramLocation = (ParamLocation) field.getAnnotation(ParamLocation.class);
            ParamLocationType paramLocationType = paramLocation.location();

            PropertyDescriptor pd = new PropertyDescriptor(methodName, paramObj.getClass(), null,
                    getSetterMethodName(methodName));
            Method setMethod = pd.getWriteMethod();

            if (paramLocationType.equals(ParamLocationType.HEADER)) {
                String fieldValue = request.getHeader(fieldName);
                setFieldValue(paramObj, field.getType(), setMethod, fieldName, fieldValue);
            } else if (paramLocationType.equals(ParamLocationType.QUERY)) {
                String fieldValue = request.getParameter(fieldName);
                setFieldValue(paramObj, field.getType(), setMethod, fieldName, fieldValue);
            } else if (paramLocationType.equals(ParamLocationType.PATH)) {
                if (pathParamMap == null) {
                    throw new Exception("Invaild path param map info.");
                }
                String fieldValue = pathParamMap.get(fieldName);
                setFieldValue(paramObj, field.getType(), setMethod, fieldName, fieldValue);
            } else if (paramLocationType.equals(ParamLocationType.BODY)) {
                if (bodyJson != null) {
                    Object fieldValue = JSONObject.parseObject(JSON.toJSONString(bodyJson), field.getType());
                    setMethod.invoke(paramObj, fieldValue);
                } else if (file != null) {
                    if (field.getType().equals(MultipartFile.class)) {
                        setMethod.invoke(paramObj, file);
                    }
                }
            }
        }

        String language = request.getHeader(HttpDefine.HEADER_X_LANGUAGE.toLowerCase());
        Locale locale = getLocale(language);

        HttpRequestInfo httpRequestInfo = new HttpRequestInfo(locale, paramObj);
        return httpRequestInfo;
    }

    private static Locale getLocale(String language) {
        Locale locale = Locale.US;
        if (LANG_EN_US.equals(language)) {
            locale = Locale.US;
        } else {
            locale = Locale.CHINA;
        }
        return locale;
    }

    /**
     * convert ws request info
     *
     * @param session  :
     * @param bodyText :
     * @param clazz    :
     * @return WsRequestInfo :
     * @throws Exception :
     */
    public static WsRequestInfo convertWsRequestInfo(WebSocketSession session, String bodyText, final Class<?> clazz)
            throws Exception {
        if (session == null || clazz == null) {
            throw new Exception("Invaild param.");
        }

        Field[] fields = clazz.getDeclaredFields();
        Object paramObj = clazz.newInstance();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (StringUtils.isEmpty(fieldName)) {
                throw new Exception("Invaild field name,field name(" + fieldName + ").");
            }
            String methodName = fieldName;
            if (field.isAnnotationPresent(JSONField.class)) {
                JSONField jsonField = (JSONField) field.getAnnotation(JSONField.class);
                String jsonFieldName = jsonField.name();
                if (!StringUtils.isEmpty(jsonFieldName)) {
                    fieldName = jsonFieldName;
                }
            }

            if (!field.isAnnotationPresent(ParamLocation.class)
                    || field.getAnnotation(ParamLocation.class) == null) {
                throw new Exception("Invaild param location,field name(" + fieldName + ").");
            }
            ParamLocation paramLocation = (ParamLocation) field.getAnnotation(ParamLocation.class);
            ParamLocationType paramLocationType = paramLocation.location();

            PropertyDescriptor pd = new PropertyDescriptor(methodName, paramObj.getClass());
            Method setMethod = pd.getWriteMethod();

            if (paramLocationType.equals(ParamLocationType.HEADER)) {
                HttpSession httpSession = (HttpSession) session.getAttributes().get(HttpSession.class.getName());
                if (httpSession == null) {
                    throw new Exception("Get http session error.");
                }

                String fieldValue = (String) httpSession.getAttribute(fieldName);
                setFieldValue(paramObj, field.getType(), setMethod, fieldName, fieldValue);
            } else if (paramLocationType.equals(ParamLocationType.QUERY)) {
                String fieldValue = (String) session.getAttributes().get(fieldName);
                setFieldValue(paramObj, field.getType(), setMethod, fieldName, fieldValue);
            } else if (paramLocationType.equals(ParamLocationType.BODY)) {
                Object fieldValue = JSONObject.parseObject(bodyText, field.getType());
                setMethod.invoke(paramObj, fieldValue);
            }
        }

        WsRequestInfo wsRequestInfo = new WsRequestInfo(paramObj);
        return wsRequestInfo;
    }

    /**
     * convert msg request info
     *
     * @param msgText :
     * @param clazz   :
     * @return MsgRequestInfo :
     * @throws Exception :
     */
    public static MsgRequestInfo convertMsgRequestInfo(String msgText, final Class<?> clazz)
            throws Exception {
        if (clazz == null) {
            throw new Exception("Invaild param.");
        }

        Field[] fields = clazz.getDeclaredFields();
        Object paramObj = clazz.newInstance();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (StringUtils.isEmpty(fieldName)) {
                throw new Exception("Invaild field name,field name(" + fieldName + ").");
            }
            String methodName = fieldName;
            if (field.isAnnotationPresent(JSONField.class)) {
                JSONField jsonField = (JSONField) field.getAnnotation(JSONField.class);
                String jsonFieldName = jsonField.name();
                if (!StringUtils.isEmpty(jsonFieldName)) {
                    fieldName = jsonFieldName;
                }
            }

            if (!field.isAnnotationPresent(ParamLocation.class)
                    || field.getAnnotation(ParamLocation.class) == null) {
                throw new Exception("Invaild param location,field name(" + fieldName + ").");
            }
            ParamLocation paramLocation = (ParamLocation) field.getAnnotation(ParamLocation.class);
            ParamLocationType paramLocationType = paramLocation.location();

            PropertyDescriptor pd = new PropertyDescriptor(methodName, paramObj.getClass());
            Method setMethod = pd.getWriteMethod();

            if (paramLocationType.equals(ParamLocationType.BODY)) {
                Object fieldValue = JSONObject.parseObject(msgText, field.getType());
                setMethod.invoke(paramObj, fieldValue);
            }
        }

        MsgRequestInfo msgRequestInfo = new MsgRequestInfo(0, paramObj, null);
        return msgRequestInfo;
    }


    private static void setFieldValue(Object paramObj, Class type, Method setMethod,
                                      String fieldName, String fieldValue)
            throws Exception {
        if (fieldValue == null) {
            //throw new Exception("Invaild field value,field name(" + fieldName + "),type(" + type.getName() + ").");
            return;
        }

        if (Integer.class.equals(type) || int.class.equals(type)) {
            Integer integerVal = Integer.valueOf(fieldValue);
            setMethod.invoke(paramObj, integerVal);
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            Long longVal = Long.valueOf(fieldValue);
            setMethod.invoke(paramObj, longVal);
        } else if (Date.class.equals(type)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = dateFormat.parse(fieldValue.toString());
            setMethod.invoke(paramObj, time);
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            Boolean boolVal = null;
            if (Boolean.TRUE.toString().equals(fieldValue)) {
                boolVal = true;
            } else if (Boolean.FALSE.toString().equals(fieldValue)) {
                boolVal = false;
            }
            setMethod.invoke(paramObj, boolVal);
        } else if (Short.class.equals(type) || short.class.equals(type)) {
            Short shortVal = Short.valueOf(fieldValue.toString());
            setMethod.invoke(paramObj, shortVal);
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            Float floatVal = Float.valueOf(fieldValue);
            setMethod.invoke(paramObj, floatVal);
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            Double doubleVal = Double.valueOf(fieldValue);
            setMethod.invoke(paramObj, doubleVal);
        } else if (BigInteger.class.equals(type)) {
            setMethod.invoke(paramObj, new BigInteger(fieldValue.toString(), RADIX_10));
        } else if (BigDecimal.class.equals(type)) {
            setMethod.invoke(paramObj, new BigDecimal(fieldValue.toString()));
        } else if (String.class.equals(type)) {
            setMethod.invoke(paramObj, fieldValue);
        } else {
            throw new Exception("Unsupport value type,field name(" + fieldName + "),type(" + type.getName() + ").");
        }
    }

    private static String getSetterMethodName(String name) {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        sb.insert(0, "set");

        return sb.toString();
    }


    private RequestInfoUtils() {
    }
}
