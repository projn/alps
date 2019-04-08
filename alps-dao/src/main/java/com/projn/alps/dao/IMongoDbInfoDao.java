package com.projn.alps.dao;

import java.util.List;

public interface IMongoDbInfoDao {
     public <T> boolean saveObjInfo(T obj);
     public <T> T getObjInfo(String id, Class<T> cls);
     public <T> Long getObjInfoCount(String itemName, Object item, Class<T> cls);
     public <T> List<T> getObjInfoList(String itemName, Object item, int beginIndex, int size, Class<T> cls);
     public <T> boolean updateObjItemInfo(String id, String itemName, Object newItem, Class<T> cls);
     public <T> boolean deleteObjInfo(String id, Class<T> cls);
}