package com.projn.alps.dao;

import java.util.List;

/**
 * mongo db info dao
 *
 * @author : sunyuecheng
 */
public interface IMongoDbInfoDao {
    /**
     * save obj info
     *
     * @param obj :
     * @param <T> :
     * @return boolean :
     */

    <T> boolean saveObjInfo(T obj);

    /**
     * get obj info
     *
     * @param id  :
     * @param cls :
     * @param <T> :
     * @return T :
     */
    <T> T getObjInfo(String id, Class<T> cls);

    /**
     * get obj info count
     *
     * @param itemName :
     * @param item     :
     * @param cls      :
     * @param <T> :
     * @return Long :
     */
    <T> Long getObjInfoCount(String itemName, Object item, Class<T> cls);

    /**
     * get obj info list
     *
     * @param itemName   :
     * @param item       :
     * @param beginIndex :
     * @param size       :
     * @param cls        :
     * @param <T> :
     * @return List<T> :
     */
    <T> List<T> getObjInfoList(String itemName, Object item, int beginIndex, int size, Class<T> cls);

    /**
     * update obj item info
     *
     * @param id       :
     * @param itemName :
     * @param newItem  :
     * @param cls      :
     * @param <T> :
     * @return boolean :
     */
    <T> boolean updateObjItemInfo(String id, String itemName, Object newItem, Class<T> cls);

    /**
     * delete obj info
     *
     * @param id  :
     * @param cls :
     * @param <T> :
     * @return boolean :
     */
    <T> boolean deleteObjInfo(String id, Class<T> cls);
}