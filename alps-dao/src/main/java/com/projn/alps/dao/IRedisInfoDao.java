package com.projn.alps.dao;

import com.projn.alps.ValueScoreInfo;
import org.springframework.data.redis.core.DefaultTypedTuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis info dao
 *
 * @author : sunyuecheng
 */
public interface IRedisInfoDao {
    /**
     * save str info
     *
     * @param key   :
     * @param value :
     * @return boolean :
     */
    boolean saveStrInfo(String key, String value);

    /**
     * save str info ex
     *
     * @param key            :
     * @param value          :
     * @param timeoutSeconds :
     * @return boolean :
     */
    boolean saveStrInfoEx(String key, String value, long timeoutSeconds);

    /**
     * get str info
     *
     * @param key :
     * @return String :
     */
    String getStrInfo(String key);

    /**
     * scan str info
     *
     * @param pattern :
     * @param count   :
     * @return List<String> :
     */
    List<String> scanStrInfo(String pattern, long count);

    /**
     * alter str byte info
     *
     * @param key    :
     * @param value  :
     * @param offset :
     * @return boolean :
     */
    boolean alterStrByteInfo(String key, byte[] value, long offset);

    /**
     * get str byte info
     *
     * @param key    :
     * @param offset :
     * @param size   :
     * @return byte[] :
     */
    byte[] getStrByteInfo(String key, long offset, long size);

    /**
     * incr int info
     *
     * @param key :
     * @return boolean :
     */
    boolean incrIntInfo(String key);

    /**
     * decr int info
     *
     * @param key :
     * @return boolean :
     */
    boolean decrIntInfo(String key);

    /**
     * save bit map info
     *
     * @param key   :
     * @param value :
     * @return boolean :
     */
    boolean saveBitMapInfo(String key, byte[] value);

    /**
     * get bit map info
     *
     * @param key :
     * @return byte[] :
     */
    byte[] getBitMapInfo(String key);

    /**
     * set bit map bit info
     *
     * @param key    :
     * @param value  :
     * @param offset :
     * @return boolean :
     */
    boolean setBitMapBitInfo(String key, boolean value, long offset);

    /**
     * get bit map bit info
     *
     * @param key    :
     * @param offset :
     * @return int :
     */
    int getBitMapBitInfo(String key, long offset);

    /**
     * save str info to list
     *
     * @param listName  :
     * @param valueList :
     * @return boolean :
     */
    boolean saveStrInfoToList(String listName, List<String> valueList);

    /**
     * get str info list from list
     *
     * @param listName   :
     * @param beginIndex :
     * @param size       :
     * @return List<String> :
     */
    List<String> getStrInfoListFromList(String listName, long beginIndex, long size);

    /**
     * get str info from list
     *
     * @param listName :
     * @param index    :
     * @return String :
     */
    String getStrInfoFromList(String listName, long index);

    /**
     * boolean delete str info from list
     *
     * @param listName :
     * @param index    :
     * @return :
     */
    boolean deleteStrInfoFromList(String listName, long index);

    /**
     * delete str info from list
     *
     * @param listName :
     * @param vaule    :
     * @return boolean :
     */
    boolean deleteStrInfoFromList(String listName, String vaule);

    /**
     * get str info list size from list
     *
     * @param listName :
     * @return long :
     */
    long getStrInfoListSizeFromList(String listName);

    /**
     * rpush str info to list
     *
     * @param listName :
     * @param value    :
     * @return boolean :
     */
    boolean rpushStrInfoToList(String listName, String value);

    /**
     * rpop str info from list
     *
     * @param listName :
     * @return String :
     */
    String rpopStrInfoFromList(String listName);

    /**
     * lpush str info to list
     *
     * @param listName :
     * @param value    :
     * @return boolean :
     */
    boolean lpushStrInfoToList(String listName, String value);

    /**
     * lpush str info to list
     *
     * @param listName :
     * @return boolean :
     */
    String lpopStrInfoFromList(String listName);

    /**
     * save obj info
     *
     * @param key :
     * @param obj :
     * @param <T> :
     * @return boolean:
     */
    <T> boolean saveObjInfo(String key, T obj);

    /**
     * get obj info
     *
     * @param key :
     * @param cls :
     * @param <T> :
     * @return T:
     */
    <T> T getObjInfo(String key, Class<T> cls);

    /**
     * get obj item info
     *
     * @param key      :
     * @param itemName :
     * @return String :
     */
    String getObjItemInfo(String key, String itemName);

    /**
     * update obj item info
     *
     * @param key      :
     * @param itemName :
     * @param item     :
     * @return boolean :
     */
    boolean updateObjItemInfo(String key, String itemName, String item);

    /**
     * save map info
     *
     * @param key :
     * @param map :
     * @return boolean :
     */
    boolean saveMapInfo(String key, Map<String, String> map);

    /**
     * get map info
     *
     * @param key :
     * @return Map<String, String> :
     */
    Map<String, String> getMapInfo(String key);

    /**
     * get map item info
     *
     * @param key      :
     * @param itemName :
     * @return String :
     */
    String getMapItemInfo(String key, String itemName);

    /**
     * update map item info
     *
     * @param key      :
     * @param itemName :
     * @param item     :
     * @return boolean :
     */
    boolean updateMapItemInfo(String key, String itemName, String item);

    /**
     * delete map item info
     *
     * @param key      :
     * @param itemName :
     * @return boolean :
     */
    boolean deleteMapItemInfo(String key, String itemName);

    /**
     * get map item info size
     *
     * @param key :
     * @return long :
     */
    long getMapItemInfoSize(String key);

    /**
     * save str info to set
     *
     * @param setName :
     * @param value   :
     * @return boolean :
     */
    boolean saveStrInfoToSet(String setName, String value);

    /**
     * pop str info from set
     *
     * @param setName :
     * @return String :
     */
    String popStrInfoFromSet(String setName);

    /**
     * delete str info from set
     *
     * @param setName :
     * @param value   :
     * @return boolean :
     */
    boolean deleteStrInfoFromSet(String setName, String value);

    /**
     * check exist str info from set
     *
     * @param setName :
     * @param value   :
     * @return boolean :
     */
    boolean checkExistStrInfoFromSet(String setName, String value);

    /**
     * get str info set size from set
     *
     * @param setName :
     * @return long :
     */
    long getStrInfoSetSizeFromSet(String setName);

    /**
     * scan str info from set
     *
     * @param setName :
     * @param pattern :
     * @param count   :
     * @return List<String> :
     */
    List<String> scanStrInfoFromSet(String setName, String pattern, long count);

    /**
     * save str info to sort set
     *
     * @param setName :
     * @param value   :
     * @param score   :
     * @return boolean :
     */
    boolean saveStrInfoToSortSet(String setName, String value, double score);

    /**
     * save str info list to sort set
     *
     * @param setName   :
     * @param valueList :
     * @return boolean :
     */
    boolean saveStrInfoListToSortSet(String setName, Set<DefaultTypedTuple<String>> valueList);

    /**
     * get score by str from sort set
     *
     * @param stName :
     * @param value  :
     * @return double :
     */
    double getScoreByStrFromSortSet(String stName, String value);

    /**
     * incr str score info
     *
     * @param setName :
     * @param value   :
     * @return boolean :
     */
    boolean incrStrScoreInfo(String setName, String value);

    /**
     * decr str score info
     *
     * @param setName :
     * @param value   :
     * @return boolean :
     */
    boolean decrStrScoreInfo(String setName, String value);

    /**
     * get str info list from sort set
     *
     * @param setName    :
     * @param beginIndex :
     * @param size       :
     * @param desc       :
     * @return Set<String> :
     */
    Set<String> getStrInfoListFromSortSet(String setName, long beginIndex, long size, boolean desc);

    /**
     * get str info list by score from sort set
     *
     * @param setName    :
     * @param min        :
     * @param max        :
     * @param beginIndex :
     * @param size       :
     * @param desc       :
     * @return Set<String> :
     */
    Set<String> getStrInfoListByScoreFromSortSet(String setName, double min, double max,
                                                 long beginIndex, long size, boolean desc);


    /**
     * get value score info list by score from sort set
     *
     * @param setName    :
     * @param min        :
     * @param max        :
     * @param beginIndex :
     * @param size       :
     * @param desc       :
     * @return Set<ValueScoreInfo> :
     */
    Set<ValueScoreInfo> getValueScoreInfoListByScoreFromSortSet(String setName, double min, double max,
                                                                long beginIndex, long size, boolean desc);

    /**
     * delete str info from sort set
     *
     * @param setName :
     * @param value   :
     * @return boolean :
     */
    boolean deleteStrInfoFromSortSet(String setName, String value);

    /**
     * delete str info by score from sort set
     *
     * @param setName :
     * @param min     :
     * @param max     :
     * @return boolean :
     */
    boolean deleteStrInfoByScoreFromSortSet(String setName, double min, double max);

    /**
     * get str info set size by score from sort set
     *
     * @param setName :
     * @param min     :
     * @param max     :
     * @return long :
     */
    long getStrInfoSetSizeByScoreFromSortSet(String setName, double min, double max);

    /**
     * get str info size from sort set
     *
     * @param setName :
     * @return long :
     */
    long getStrInfoSizeFromSortSet(String setName);

    /**
     * scan str info from sort set
     *
     * @param setName :
     * @param pattern :
     * @param count   :
     * @return List<String> :
     */
    List<String> scanStrInfoFromSortSet(String setName, String pattern, long count);

    /**
     * get keys
     *
     * @param pattern :
     * @return Set<String> :
     */
    Set<String> getKeys(String pattern);

    /**
     * delete key
     *
     * @param key :
     * @return boolean :
     */
    boolean deleteKey(String key);

    /**
     * expire key
     *
     * @param key            :
     * @param timeoutSeconds :
     * @return boolean :
     */
    boolean expireKey(String key, long timeoutSeconds);

    /**
     * expire key at
     *
     * @param key         :
     * @param expiredTime :
     * @return boolean :
     */
    boolean expireKeyAt(String key, long expiredTime);

    /**
     * get expire
     *
     * @param key :
     * @return long :
     */
    long getExpire(String key);

    /**
     * exists
     *
     * @param key :
     * @return boolean :
     */
    boolean exists(String key);
}