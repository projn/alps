package com.sct.mongodbtest.dao;

import com.sct.mongodbtest.entity.EntityInfo;

import java.util.List;

public interface IMongoDbInfoDao {
     public boolean saveObjInfo(EntityInfo obj);
     public EntityInfo getObjInfo(String id);
     public Long getObjInfoCount(String itemName, Object item);
     public List<EntityInfo> getObjInfoList(String itemName, Object item, int beginIndex, int size);
     public boolean updateObjItemInfo(String itemName, Object item, Object newItem);
     public boolean deleteObjInfo(String id);
}