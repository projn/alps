package com.sct.mongodbtest.entity;

import java.io.Serializable;

public class EntityInfo implements Serializable {
    private String id = null;
    private String strItem = null;
    private Integer intItem = null;
    private Long createDate = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStrItem() {
        return strItem;
    }

    public void setStrItem(String strItem) {
        this.strItem = strItem;
    }

    public Integer getIntItem() {
        return intItem;
    }

    public void setIntItem(Integer intItem) {
        this.intItem = intItem;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }
}
