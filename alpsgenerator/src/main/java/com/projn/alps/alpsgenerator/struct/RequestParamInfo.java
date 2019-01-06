package com.projn.alps.alpsgenerator.struct;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * request param info
 *
 * @author : sunyuecheng
 */
public class RequestParamInfo {

    private String name;
    private String in;
    private String description;
    private boolean required;

    private String type;
    private String format;
    @JSONField(name = "enum")
    private List<String> enums;
    @JSONField(name = "default")
    private String defaultValue;

    private Map<String, Object> schema;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<String> getEnums() {
        return enums;
    }

    public void setEnums(List<String> enums) {
        this.enums = enums;
    }

    public Map<String, Object> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * schema type info
     *
     * @author : sunyuecheng
     */
    public static class SchemaTypeInfo {
        private String type;
        private String format;
        @JSONField(name = "enum")
        private List<String> enums;
        @JSONField(name = "default")
        private String defaultValue;

        @JSONField(name = "$ref")
        private String ref;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public List<String> getEnums() {
            return enums;
        }

        public void setEnums(List<String> enums) {
            this.enums = enums;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }
}
