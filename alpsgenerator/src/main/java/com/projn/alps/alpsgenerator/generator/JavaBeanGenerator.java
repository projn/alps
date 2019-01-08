package com.projn.alps.alpsgenerator.generator;

import com.projn.alps.alpsgenerator.api.AbstractGeneratedJavaFile;
import com.projn.alps.alpsgenerator.api.dom.java.*;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

import static com.projn.alps.define.CommonDefine.DEFAULT_ENCODING;

/**
 * java bean generator
 *
 * @author : sunyuecheng
 */
public final class JavaBeanGenerator {

    private static final String JAVA_CALSS_DOC_FROMAT = "/**%n"
            + " * %s%n"
            + " * @author : auto%n"
            + " */";

    private static final String JAVA_FIELD_DOC_FROMAT = "/**%n"
            + "     * %s%n"
            + "     */";

    private static final String JAVA_FIELD_JSON_ANNOUNCE_FROMAT = "@JSONField(name = \"%s\")";

    private static final String JAVA_ENUM_CLASS_NAME_FROMAT = "%sEnum";

    private static final String DEFAULT_FIELD_NAME = "value";

    private static final String DEFAULT_NUM_ENUM_FIELD_NAME_FORMAT = "ENUM_FIELD_NAME_%s(%s)";

    private static final String DEFAULT_STR_ENUM_FIELD_NAME_FORMAT = "ENUM_FIELD_NAME_%s(\"%s\")";

    private static final String NUM_ENUM_FIELD_NAME_FORMAT = "%s(%s)";

    private static final String STR_ENUM_FIELD_NAME_FORMAT = "%s(\"%s\")";

    /**
     * get clazz
     *
     * @param classTypeStr :
     * @param description  :
     * @return TopLevelClass :
     */
    public static TopLevelClass getClazz(String classTypeStr, String description) {
        FullyQualifiedJavaType classType = new FullyQualifiedJavaType(classTypeStr);
        TopLevelClass topLevelClass = new TopLevelClass(classType);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addJavaDocLine(String.format(JAVA_CALSS_DOC_FROMAT, description == null ? "" : description));

        return topLevelClass;
    }

    /**
     * transfer field type
     *
     * @param type   :
     * @param format :
     * @return String :
     */
    public static String transferFieldType(String type, String format) {
        String fieldType = null;
        switch (type) {
            case "integer":
                if (format == null || "int32".equals(format)) {
                    fieldType = Integer.class.getTypeName();
                } else if ("int64".equals(format)) {
                    fieldType = Long.class.getTypeName();
                }
                break;
            case "number":
                if ("float".equals(format)) {
                    fieldType = Float.class.getTypeName();
                } else if ("double".equals(format)) {
                    fieldType = Double.class.getTypeName();
                }
                break;
            case "string":
                if ("binary".equals(format)) {
                    fieldType = "byte[]";
                } else {
                    fieldType = String.class.getTypeName();
                }
                break;
            case "boolean":
                fieldType = boolean.class.getTypeName();
                break;
            case "array":
                fieldType = List.class.getTypeName();
                break;
            case "object":
                fieldType = Object.class.getTypeName();
                break;
            default:
                break;
        }
        return fieldType;
    }

    /**
     * get camel case string
     *
     * @param inputString             :
     * @param firstCharacterUppercase :
     * @return String :
     */
    public static String getCamelCaseString(String inputString,
                                            boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                case '{':
                case '}':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;

                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        } else {
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * get field
     *
     * @param name         :
     * @param description  :
     * @param fieldTypeStr :
     * @return Field :
     */
    public static Field getField(String name, String description, String fieldTypeStr) {
        String fieldName = getCamelCaseString(name, false);

        FullyQualifiedJavaType fieldType = new FullyQualifiedJavaType(fieldTypeStr);
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fieldType);
        field.setName(fieldName);
        if (!StringUtils.isEmpty(description)) {
            field.addJavaDocLine(String.format(JAVA_FIELD_DOC_FROMAT, description));
        }

        if (!name.equals(fieldName)) {
            field.addAnnotation(String.format(JAVA_FIELD_JSON_ANNOUNCE_FROMAT, name));
        }

        return field;
    }

    /**
     * get field
     *
     * @param name         :
     * @param description  :
     * @param fieldTypeStr :
     * @param defaultValue :
     * @return Field :
     */
    public static Field getField(String name, String description, String fieldTypeStr, String defaultValue) {
        String fieldName = getCamelCaseString(name, false);

        FullyQualifiedJavaType fieldType = new FullyQualifiedJavaType(fieldTypeStr);
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fieldType);
        field.setName(fieldName);
        if(fieldTypeStr.equals(String.class.getTypeName())) {
            field.setInitializationString("\"" +defaultValue + "\"");
        } else if(fieldTypeStr.equals(Long.class.getTypeName())) {
            field.setInitializationString(defaultValue +"L");
        } else {
            field.setInitializationString(defaultValue);
        }
        if (!StringUtils.isEmpty(description)) {
            field.addJavaDocLine(String.format(JAVA_FIELD_DOC_FROMAT, description));
        }

        if (!name.equals(fieldName)) {
            field.addAnnotation(String.format(JAVA_FIELD_JSON_ANNOUNCE_FROMAT, name));
        }

        return field;
    }

    /**
     * method get field getter method
     *
     * @param name         :
     * @param fieldTypeStr :
     * @return :
     */
    public static Method getFieldGetterMethod(String name, String fieldTypeStr) {
        String fieldName = getCamelCaseString(name, false);

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(fieldTypeStr);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        method.setName(getGetterMethodName(fieldName, fieldTypeStr));

        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(fieldName);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    /**
     * get getter method name
     *
     * @param name         :
     * @param fieldTypeStr :
     * @return String :
     */
    private static String getGetterMethodName(String name, String fieldTypeStr) {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        if (fieldTypeStr.equals(boolean.class.getTypeName())) {
            sb.insert(0, "is");
        } else {
            sb.insert(0, "get");
        }

        return sb.toString();
    }

    /**
     * get field setter method
     *
     * @param name         :
     * @param fieldTypeStr :
     * @return Method :
     */
    public static Method getFieldSetterMethod(String name, String fieldTypeStr) {
        String fieldName = JavaBeanGenerator.getCamelCaseString(name, false);

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(fieldTypeStr);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getSetterMethodName(fieldName));
        method.addParameter(new Parameter(fqjt, fieldName));

        StringBuilder sb = new StringBuilder();
        sb.append("this.");
        sb.append(fieldName);
        sb.append(" = ");
        sb.append(fieldName);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    /**
     * get setter method name
     *
     * @param name :
     * @return String :
     */
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

    /**
     * get inner enum
     *
     * @param name         :
     * @param fieldTypeStr :
     * @return InnerEnum :
     */
    public static InnerEnum getInnerEnum(String name, String fieldTypeStr) {
        String fieldName = JavaBeanGenerator.getCamelCaseString(name, true);

        String enumClassName = String.format(JAVA_ENUM_CLASS_NAME_FROMAT, fieldName);

        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(enumClassName);

        InnerEnum innerEnum = new InnerEnum(fqjt);
        innerEnum.setVisibility(JavaVisibility.PUBLIC);

        Field field = getField(DEFAULT_FIELD_NAME, null, fieldTypeStr);
        innerEnum.addField(field);

        Method method = new Method();
        method.setVisibility(JavaVisibility.DEFAULT);
        method.setConstructor(true);
        method.setName(enumClassName);

        FullyQualifiedJavaType parameterJavaType = new FullyQualifiedJavaType(fieldTypeStr);
        Parameter parameter = new Parameter(parameterJavaType, DEFAULT_FIELD_NAME);

        method.addParameter(parameter);

        StringBuilder sb = new StringBuilder();
        sb.append("this.");
        sb.append(DEFAULT_FIELD_NAME);
        sb.append(" = ");
        sb.append(DEFAULT_FIELD_NAME);
        sb.append(';');
        method.addBodyLine(sb.toString());

        innerEnum.addMethod(method);

        Method valueMethod = getFieldGetterMethod(DEFAULT_FIELD_NAME, fieldTypeStr);
        innerEnum.addMethod(valueMethod);

        return innerEnum;
    }

    /**
     * add inner enum value
     *
     * @param enumClazz    :
     * @param name         :
     * @param value        :
     * @param fieldTypeStr :
     */
    public static void addInnerEnumValue(InnerEnum enumClazz, String name, String value, String fieldTypeStr) {
        if (Integer.class.getTypeName().equals(fieldTypeStr)
                || Long.class.getTypeName().equals(fieldTypeStr)
                || Float.class.getTypeName().equals(fieldTypeStr)
                || Double.class.getTypeName().equals(fieldTypeStr)) {

            if (StringUtils.isEmpty(name)) {
                enumClazz.addEnumConstant(String.format(DEFAULT_NUM_ENUM_FIELD_NAME_FORMAT, value, value));
            } else {
                enumClazz.addEnumConstant(String.format(NUM_ENUM_FIELD_NAME_FORMAT, name, value));
            }

        } else if (String.class.getTypeName().equals(fieldTypeStr)) {
            if (StringUtils.isEmpty(name)) {
                enumClazz.addEnumConstant(String.format(DEFAULT_STR_ENUM_FIELD_NAME_FORMAT, value, value));
            } else {
                enumClazz.addEnumConstant(String.format(STR_ENUM_FIELD_NAME_FORMAT, name, value));
            }
        }

    }

    /**
     * write generated java file
     *
     * @param gjf :
     * @param recoverExist :
     * @throws Exception :
     */
    public static void writeGeneratedJavaFile(AbstractGeneratedJavaFile gjf, boolean recoverExist)
            throws Exception {
        File targetFile;
        String source;
        try {
            File directory = getDirectory(gjf
                    .getTargetProject(), gjf.getTargetPackage());
            targetFile = new File(directory, gjf.getFileName());
            source = gjf.getFormattedContent();
            if(!targetFile.exists() || recoverExist) {
                writeFile(targetFile, source, gjf.getFileEncoding());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * get directory
     *
     * @param targetProject :
     * @param targetPackage :
     * @return File :
     * @throws Exception :
     */
    public static File getDirectory(String targetProject, String targetPackage)
            throws Exception {
        // targetProject is interpreted as a directory that must exist
        //
        // targetPackage is interpreted as a sub directory, but in package
        // format (with dots instead of slashes). The sub directory will be
        // created
        // if it does not already exist

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            throw new Exception("There is no target project dir existed.");
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new Exception("Create dir error.");
            }
        }

        return directory;
    }

    /**
     * write file
     *
     * @param file         :
     * @param content      :
     * @param fileEncoding :
     * @throws IOException :
     */
    public static void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos, DEFAULT_ENCODING);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    private JavaBeanGenerator() {
    }
}
