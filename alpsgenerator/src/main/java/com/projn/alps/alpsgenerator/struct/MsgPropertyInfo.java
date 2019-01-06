package com.projn.alps.alpsgenerator.struct;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * msg property info
 *
 * @author : sunyuecheng
 */
public class MsgPropertyInfo {
    private static final String DEFAULT_TYPE = "object";

    private String type = DEFAULT_TYPE;
    private String format;
    private String description;
    @JSONField(name = "default")
    private String defaultValue;

    @JSONField(name = "enum")
    private List<String> enumValueList;

    private Map<String, String> items;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getEnumValueList() {
        return enumValueList;
    }

    public void setEnumValueList(List<String> enumValueList) {
        this.enumValueList = enumValueList;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }
}
