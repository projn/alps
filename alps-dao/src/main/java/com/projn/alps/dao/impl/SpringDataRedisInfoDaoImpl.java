package com.projn.alps.dao.impl;

import com.projn.alps.dao.IRedisInfoDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring data redis info dao impl
 *
 * @author : sunyuecheng
 */
@Repository("SpringDataRedisInfoDao")
public class SpringDataRedisInfoDaoImpl implements IRedisInfoDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDataRedisInfoDaoImpl.class);

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int COLLECTION_INIT_SIZE = 16;
    private static final int BIT_LEN = 8;

    @Autowired(required = false)
    protected RedisTemplate redisTemplate = null;

    @Override
    /**
     * save str info
     * @param key :
     * @param value :
     * @return boolean :
     */
    public boolean saveStrInfo(String key, String value) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            LOGGER.error("Save str info error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * save str info ex
     * @param key :
     * @param value :
     * @param timeoutSeconds :
     * @return boolean :
     */
    public boolean saveStrInfoEx(String key, String value, long timeoutSeconds) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("Save str info error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * get str info
     * @param key :
     * @return java.lang.String :
     */
    public String getStrInfo(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        String value = null;
        try {
            value = (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            LOGGER.error("Get str info error, key(" + key + "),error info(" + e.getMessage() + ").");
        }
        return value;
    }

    @Override
    /**
     * scan str info
     * @param pattern :
     * @param count :
     * @return List<String> :
     */
    public List<String> scanStrInfo(String pattern, long count) {
        if (StringUtils.isEmpty(pattern) || count <= 0) {
            LOGGER.error("Invaild param.");
            return null;
        }

        List<String> valueList = null;
        try {
            valueList = (List<String>) redisTemplate.execute(new RedisCallback<List<String>>() {
                @Override
                public List<String> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    List<String> subValueList = new ArrayList<>();
                    try {
                        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(count).build();
                        Cursor<byte[]> cursor = redisConnection.scan(scanOptions);
                        while (cursor.hasNext()) {
                            subValueList.add(new String(cursor.next(), DEFAULT_ENCODING));
                        }
                    } catch (Exception e) {
                        LOGGER.error("Scan str info error, error info(" + e.getMessage() + ").");
                        return null;
                    }
                    return subValueList;
                }
            });
            return valueList;
        } catch (Exception e) {
            LOGGER.error("Scan str info error, error info(" + e.getMessage() + ").");
        }

        return null;

    }

    @Override
    /**
     * alter str byte info
     * @param key :
     * @param value :
     * @param offset :
     * @return boolean :
     */
    public boolean alterStrByteInfo(final String key, final byte[] value, final long offset) {
        if (StringUtils.isEmpty(key) || value == null || offset < 0) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            String temp = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(temp)) {
                LOGGER.error("Invaild str value.");
                return false;
            }
            if (offset >= temp.length() || (value.length + offset) >= temp.length()) {
                LOGGER.error("Invaild param.");
                return false;
            }

            redisTemplate.execute(new RedisCallback<Void>() {
                @Override
                public Void doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    byte[] keyByte = null;
                    try {
                        keyByte = key.getBytes(DEFAULT_ENCODING);
                    } catch (Exception e) {
                        LOGGER.debug(e.getMessage());
                    }
                    if( keyByte != null ) {
                        redisConnection.setRange(keyByte, value, offset);
                    }
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            LOGGER.error("Alter str byte info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get str byte info
     * @param key :
     * @param offset :
     * @param size :
     * @return byte[] :
     */
    public byte[] getStrByteInfo(final String key, final long offset, final long size) {
        if (StringUtils.isEmpty(key) || offset < 0L || size <= 0L) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            String temp = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(temp)) {
                LOGGER.error("Invaild str value.");
                return null;
            }
            if (offset >= temp.length() || (size + offset) >= temp.length()) {
                LOGGER.error("Invaild param.");
                return null;
            }

            return (byte[]) redisTemplate.execute(new RedisCallback<byte[]>() {
                @Override
                public byte[] doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    byte[] keyByte = null;
                    try {
                        keyByte = key.getBytes(DEFAULT_ENCODING);
                    } catch (Exception e) {
                        LOGGER.debug(e.getMessage());
                    }
                    return redisConnection.getRange(keyByte, offset, offset + size);

                }
            });
        } catch (Exception e) {
            LOGGER.error("Get str byte info error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * incr int info
     * @param key :
     * @return boolean :
     */
    public boolean incrIntInfo(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForValue().increment(key, 1L);
        } catch (Exception e) {
            LOGGER.error("Incr int info error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * decr int info
     * @param key :
     * @return boolean :
     */
    public boolean decrIntInfo(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForValue().increment(key, -1L);
        } catch (Exception e) {
            LOGGER.error("Decr int info error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * save bit map info
     * @param key :
     * @param value :
     * @return boolean :
     */
    public boolean saveBitMapInfo(String key, byte[] value) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForValue().set(key.getBytes(DEFAULT_ENCODING), value);
            return true;
        } catch (Exception e) {
            LOGGER.error("Save bitmap info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get bit map info
     * @param key :
     * @return byte[] :
     */
    public byte[] getBitMapInfo(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        byte[] value = null;
        try {
            value = (byte[]) redisTemplate.opsForValue().get(key.getBytes(DEFAULT_ENCODING));
        } catch (Exception e) {
            LOGGER.error("Get bitmap info error, key(" + key + "),error info(" + e.getMessage() + ").");
        }
        return value;
    }

    @Override
    /**
     * set bit map bit info
     * @param key :
     * @param value :
     * @param offset :
     * @return boolean :
     */
    public boolean setBitMapBitInfo(String key, boolean value, long offset) {
        if (StringUtils.isEmpty(key) || offset < 0L) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            byte[] temp = (byte[]) redisTemplate.opsForValue().get(key.getBytes(DEFAULT_ENCODING));
            if (temp == null) {
                LOGGER.error("Invaild bitmap value.");
                return false;
            }
            if (offset >= temp.length * BIT_LEN) {
                LOGGER.error("Invaild param.");
                return false;
            }
            redisTemplate.opsForValue().setBit(key, offset, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("Set bitmap bit info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get bit map bit info
     * @param key :
     * @param offset :
     * @return int :
     */
    public int getBitMapBitInfo(String key, long offset) {
        if (StringUtils.isEmpty(key) || offset < 0L) {
            LOGGER.error("Invaild param.");
            return -1;
        }

        try {
            byte[] temp = (byte[]) redisTemplate.opsForValue().get(key.getBytes(DEFAULT_ENCODING));
            if (temp == null) {
                LOGGER.error("Invaild bitmap value.");
                return -1;
            }
            if (offset >= temp.length * BIT_LEN) {
                LOGGER.error("Invaild param.");
                return -1;
            }
            Boolean ret = redisTemplate.opsForValue().getBit(key.getBytes(DEFAULT_ENCODING), offset);
            return ret == null ? 0 : (ret ? 1 : 0);
        } catch (Exception e) {
            LOGGER.error("Get bitmap bit info error, error info(" + e.getMessage() + ").");
        }
        return -1;
    }

    @Override
    /**
     * save str info to list
     * @param listName :
     * @param valueList :
     * @return boolean :
     */
    public boolean saveStrInfoToList(final String listName, final List<String> valueList) {
        if (StringUtils.isEmpty(listName)) {
            LOGGER.error("Invaild param.");
            return false;
        }
        if (valueList.isEmpty()) {
            return true;
        }

        try {

            RedisCallback<List<Object>> pipelineCallback = new RedisCallback<List<Object>>() {
                @Override
                public List<Object> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    redisConnection.openPipeline();
                    byte[] keyByte = null;
                    try {
                        keyByte = listName.getBytes(DEFAULT_ENCODING);

                        for (int i = 0; i < valueList.size(); i++) {
                            redisConnection.rPush(keyByte, valueList.get(i).getBytes(DEFAULT_ENCODING));
                        }
                    } catch (Exception e) {
                        LOGGER.debug(e.getMessage());
                    }

                    return redisConnection.closePipeline();
                }

            };

            List<Object> results = (List<Object>) redisTemplate.execute(pipelineCallback);

//            RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
//            RedisConnection redisConnection = factory.getConnection();
//            List<Object> results = null;
//            redisConnection.openPipeline();
//            for(int i=0; i< valueList.size();i++) {
//                redisConnection.rPush(listName.getBytes(),valueList.get(i).getBytes());
//            }
//            results = redisConnection.closePipeline();
//            RedisConnectionUtils.releaseConnection(redisConnection, factory);

            if (results == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Save str info to list error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get str info list from list
     * @param listName :
     * @param beginIndex :
     * @param size :
     * @return java.util.List<java.lang.String> :
     */
    public List<String> getStrInfoListFromList(String listName, long beginIndex, long size) {
        if (StringUtils.isEmpty(listName) || beginIndex < 0L) {
            LOGGER.error("Invaild param.");
            return null;
        }

        if (size == 0L) {
            return new ArrayList<String>();
        }

        try {
            if (size == -1L) {
                Long totalSize = redisTemplate.opsForList().size(listName);
                if( totalSize == null) {
                    LOGGER.error("Invaild param.");
                    return null;
                }
                size = totalSize - beginIndex;
            }

            List<String> strInfoList =
                    redisTemplate.opsForList().range(listName, beginIndex, beginIndex + size - 1L);
            return strInfoList;
        } catch (Exception e) {
            LOGGER.error("Get str info list from list error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * get str info from list
     * @param listName :
     * @param index :
     * @return java.lang.String :
     */
    public String getStrInfoFromList(String listName, long index) {
        if (StringUtils.isEmpty(listName) || index < 0L) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            Long totalSize = redisTemplate.opsForList().size(listName);
            if (totalSize == null || index >= totalSize ) {
                LOGGER.error("Invaild param.");
                return null;
            }
            return (String) redisTemplate.opsForList().index(listName, index);
        } catch (Exception e) {
            LOGGER.error("Get str info from list error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * delete str info from list
     * @param listName :
     * @param index :
     * @return boolean :
     */
    public boolean deleteStrInfoFromList(String listName, long index) {
        if (StringUtils.isEmpty(listName) || index < -1L) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            Long totalSize = redisTemplate.opsForList().size(listName);
            if (index >= 0) {
                if ( totalSize == null || index >= totalSize ) {
                    LOGGER.error("Invaild param.");
                    return false;
                }
                redisTemplate.opsForList().trim(listName, index, index + 1L);
            } else {
                redisTemplate.opsForList().trim(listName, 0L, totalSize - 1L);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete str info from list error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * delete str info from list
     * @param listName :
     * @param vaule :
     * @return boolean :
     */
    public boolean deleteStrInfoFromList(String listName, String vaule) {
        if (StringUtils.isEmpty(listName) || StringUtils.isEmpty(vaule)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForList().remove(listName, 0L, vaule);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete str info from list error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get str info list size from list
     * @param listName :
     * @return long :
     */
    public long getStrInfoListSizeFromList(String listName) {
        if (StringUtils.isEmpty(listName)) {
            LOGGER.error("Invaild param.");
            return 0L;
        }
        try {
            Long totalSize = redisTemplate.opsForList().size(listName);
            return totalSize == null ? 0L : totalSize;
        } catch (Exception e) {
            LOGGER.error("Get str info list size from list error, error info(" + e.getMessage() + ").");
        }
        return 0L;
    }

    @Override
    /**
     * rpush str info to list
     * @param listName :
     * @param value :
     * @return boolean :
     */
    public boolean rpushStrInfoToList(String listName, String value) {
        if (StringUtils.isEmpty(listName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            Long ret = redisTemplate.opsForList().rightPush(listName, value);
            return ret == null ? false : (ret > 0L);
        } catch (Exception e) {
            LOGGER.error("Push str info to list error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * rpop str info from list
     * @param listName :
     * @return java.lang.String :
     */
    public String rpopStrInfoFromList(String listName) {
        if (StringUtils.isEmpty(listName)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            return (String) redisTemplate.opsForList().rightPop(listName);
        } catch (Exception e) {
            LOGGER.error("Pop str info from list error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * lpush str info to list
     * @param listName :
     * @param value :
     * @return boolean :
     */
    public boolean lpushStrInfoToList(String listName, String value) {
        if (StringUtils.isEmpty(listName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            Long ret = redisTemplate.opsForList().leftPush(listName, value);
            return ret == null ? false : (ret > 0L);
        } catch (Exception e) {
            LOGGER.error("Push str info to list error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * lpop str info from list
     * @param listName :
     * @return java.lang.String :
     */
    public String lpopStrInfoFromList(String listName) {
        if (StringUtils.isEmpty(listName)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            return (String) redisTemplate.opsForList().leftPop(listName);
        } catch (Exception e) {
            LOGGER.error("Pop str info from list error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * save obj info
     * @param key :
     * @param obj :
     * @return boolean :
     */
    public <T> boolean saveObjInfo(String key, T obj) {
        if (StringUtils.isEmpty(key) || obj == null) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            Map data = objectToHashMap(obj);
            redisTemplate.opsForHash().putAll(key, data);
            return true;
        } catch (Exception e) {
            LOGGER.error("Save obj info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get obj info
     * @param key :
     * @param cls :
     * @return T :
     */
    public <T> T getObjInfo(String key, Class<T> cls) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        T retObj = null;
        try {
            Map<String, String> data = redisTemplate.opsForHash().entries(key);
            if (data != null) {
                retObj = mapToObject(data, cls);
            }
        } catch (Exception e) {
            LOGGER.error("Get obj info error, error info(" + e.getMessage() + ").");
        }
        return retObj;
    }

    @Override
    /**
     * get obj item info
     * @param key :
     * @param itemName :
     * @return java.lang.String :
     */
    public String getObjItemInfo(String key, String itemName) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(itemName)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        String retObj = null;
        try {
            retObj = (String) redisTemplate.opsForHash().get(key, itemName);

            return retObj;
        } catch (Exception e) {
            LOGGER.error("Get obj item info error, error info(" + e.getMessage() + ").");
        }
        return retObj;
    }

    @Override
    /**
     * update obj item info
     * @param key :
     * @param itemName :
     * @param item :
     * @return boolean :
     */
    public boolean updateObjItemInfo(String key, String itemName, String item) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(itemName)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForHash().put(key, itemName, item);
            return true;
        } catch (Exception e) {
            LOGGER.error("Update obj item info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * save map info
     * @param key :
     * @param map :
     * @return boolean :
     */
    public boolean saveMapInfo(String key, Map<String, String> map) {
        if (StringUtils.isEmpty(key) || map == null) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            LOGGER.error("Save map info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get map info
     * @param key :
     * @return java.util.Map<java.lang.String , java.lang.String> :
     */
    public Map<String, String> getMapInfo(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            Map<String, String> data = redisTemplate.opsForHash().entries(key);
            return data;
        } catch (Exception e) {
            LOGGER.error("Get map info error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * get map item info
     * @param key :
     * @param itemName :
     * @return java.lang.String :
     */
    public String getMapItemInfo(String key, String itemName) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(itemName)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        String retObj = null;
        try {
            Object obj = redisTemplate.opsForHash().get(key, itemName);
            if (obj != null) {
                retObj = (String) obj;
            }

            return retObj;
        } catch (Exception e) {
            LOGGER.error("Get map item info error, error info(" + e.getMessage() + ").");
        }
        return retObj;
    }

    @Override
    /**
     * update map item info
     * @param key :
     * @param itemName :
     * @param item :
     * @return boolean :
     */
    public boolean updateMapItemInfo(String key, String itemName, String item) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(itemName)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForHash().put(key, itemName, item);
            return true;
        } catch (Exception e) {
            LOGGER.error("Update obj item info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * delete map item info
     * @param key :
     * @param itemName :
     * @return boolean :
     */
    public boolean deleteMapItemInfo(String key, String itemName) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(itemName)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForHash().delete(key, itemName);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete obj item info error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get map item info size
     * @param key :
     * @return long :
     */
    public long getMapItemInfoSize(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return 0L;
        }

        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            LOGGER.error("Delete obj item info error, error info(" + e.getMessage() + ").");
        }
        return 0L;
    }

    @Override
    /**
     * save str info to set
     * @param setName :
     * @param value :
     * @return boolean :
     */
    public boolean saveStrInfoToSet(String setName, String value) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForSet().add(setName, value);
        } catch (Exception e) {
            LOGGER.error("Save str info to set error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * pop str info from set
     * @param setName :
     * @return java.lang.String :
     */
    public String popStrInfoFromSet(String setName) {
        if (StringUtils.isEmpty(setName)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            return (String) redisTemplate.opsForSet().pop(setName);
        } catch (Exception e) {
            LOGGER.error("Pop str info from set error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * delete str info from set
     * @param setName :
     * @param value :
     * @return boolean :
     */
    public boolean deleteStrInfoFromSet(String setName, String value) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForSet().remove(setName, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete str info from set error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * check exist str info from set
     * @param setName :
     * @param value :
     * @return boolean :
     */
    public boolean checkExistStrInfoFromSet(String setName, String value) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            Boolean ret = redisTemplate.opsForSet().isMember(setName, value);
            return ret == null ? false : ret;
        } catch (Exception e) {
            LOGGER.error("Check exist str info from set error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get str info set size from set
     * @param setName :
     * @return long :
     */
    public long getStrInfoSetSizeFromSet(String setName) {
        if (StringUtils.isEmpty(setName)) {
            LOGGER.error("Invaild param.");
            return 0L;
        }

        try {
            Long ret = redisTemplate.opsForSet().size(setName);
            return ret == null ? 0L : ret;
        } catch (Exception e) {
            LOGGER.error("Get set size error, error info(" + e.getMessage() + ").");
        }
        return 0L;
    }

    @Override
    /**
     * scan str info from set
     * @param setName :
     * @param pattern :
     * @param count :
     * @return java.util.List<java.lang.String> :
     */
    public List<String> scanStrInfoFromSet(String setName, String pattern, long count) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(pattern)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(count).build();
        try (Cursor<String> curosr =
                     redisTemplate.opsForSet().scan(setName, scanOptions)) {

            List<String> valueList = new ArrayList<>();
            while (curosr.hasNext()) {
                valueList.add(curosr.next());
            }
            return valueList;
        } catch (Exception e) {
            LOGGER.error("Scan str info from set error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * save str info to sort set
     * @param setName :
     * @param value :
     * @param score :
     * @return boolean :
     */
    public boolean saveStrInfoToSortSet(String setName, String value, double score) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForZSet().add(setName, value, score);
        } catch (Exception e) {
            LOGGER.error("Save str info to sort set error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * save str info list to sort set
     * @param setName :
     * @param valueList :
     * @return boolean :
     */
    public boolean saveStrInfoListToSortSet(String setName, Set<DefaultTypedTuple<String>> valueList) {
        if (StringUtils.isEmpty(setName) || valueList == null || valueList.isEmpty()) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForZSet().add(setName, valueList);
        } catch (Exception e) {
            LOGGER.error("Save str info to sort set error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * get score by str from sort set
     * @param stName :
     * @param value :
     * @return double :
     */
    public double getScoreByStrFromSortSet(String stName, String value) {
        if (StringUtils.isEmpty(stName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return 0;
        }
        try {
            Double ret = redisTemplate.opsForZSet().score(stName, value);
            return ret == null ? 0 : ret;
        } catch (Exception e) {
            LOGGER.error("Get sort set value score error, error info(" + e.getMessage() + ").");
        }
        return 0;
    }

    @Override
    /**
     * incr str score info
     * @param setName :
     * @param value :
     * @return boolean :
     */
    public boolean incrStrScoreInfo(String setName, String value) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForZSet().incrementScore(setName, value, 1);
        } catch (Exception e) {
            LOGGER.error("Incr str score info error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * decr str score info
     * @param setName :
     * @param value :
     * @return boolean :
     */
    public boolean decrStrScoreInfo(String setName, String value) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForZSet().incrementScore(setName, value, -1L);
        } catch (Exception e) {
            LOGGER.error("Decr str score info error, error info(" + e.getMessage() + ").");
            return false;
        }
        return true;
    }

    @Override
    /**
     * get str info list from sort set
     * @param setName :
     * @param beginIndex :
     * @param size :
     * @param desc :
     * @return java.util.Set<java.lang.String> :
     */
    public Set<String> getStrInfoListFromSortSet(String setName, long beginIndex, long size, boolean desc) {
        if (StringUtils.isEmpty(setName) || beginIndex < 0L) {
            LOGGER.error("Invaild param.");
            return null;
        }

        if (size == 0) {
            return new HashSet<>();
        }

        try {
            if (size == -1) {
                Long totalSize = redisTemplate.opsForZSet().size(setName);
                if(totalSize == null) {
                    LOGGER.error("Invaild param.");
                    return null;
                }
                size = totalSize - beginIndex;
            }

            Set<String> strInfoList = null;
            if (desc) {
                strInfoList = redisTemplate.opsForZSet().range(setName, beginIndex, beginIndex + size - 1L);
            } else {
                strInfoList = redisTemplate.opsForZSet().reverseRange(setName, beginIndex, beginIndex + size - 1L);
            }
            return strInfoList;
        } catch (Exception e) {
            LOGGER.error("Get str info list from sort set error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * get str info list by score from sort set
     * @param setName :
     * @param min :
     * @param max :
     * @param beginIndex :
     * @param size :
     * @param desc :
     * @return java.util.Set<java.lang.String> :
     */
    public Set<String> getStrInfoListByScoreFromSortSet(String setName,
                                                        double min, double max,
                                                        long beginIndex, long size, boolean desc) {
        if (StringUtils.isEmpty(setName) || beginIndex < 0L) {
            LOGGER.error("Invaild param.");
            return null;
        }

        if (size == 0) {
            return new HashSet<>();
        }
        try {
            if (size == -1) {
                Long totalSize = redisTemplate.opsForZSet().size(setName);
                if(totalSize == null) {
                    LOGGER.error("Invaild param.");
                    return null;
                }
                size = totalSize - beginIndex;
            }
            Set<String> strInfoList = null;
            if (desc) {
                strInfoList = redisTemplate.opsForZSet().rangeByScore(setName, min, max, beginIndex, size);
            } else {
                strInfoList = redisTemplate.opsForZSet().reverseRangeByScore(setName, min, max, beginIndex, size);
            }
            return strInfoList;
        } catch (Exception e) {
            LOGGER.error("Get str info list from sort set error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * delete str info from sort set
     * @param setName :
     * @param value :
     * @return boolean :
     */
    public boolean deleteStrInfoFromSortSet(String setName, String value) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(value)) {
            LOGGER.error("Invaild param.");
            return false;
        }
        try {
            redisTemplate.opsForZSet().remove(setName, value);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete str info from sort set error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * delete str info by score from sort set
     * @param setName :
     * @param min :
     * @param max :
     * @return boolean :
     */
    public boolean deleteStrInfoByScoreFromSortSet(String setName, double min, double max) {
        if (StringUtils.isEmpty(setName)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.opsForZSet().removeRangeByScore(setName, min, max);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete str info by score from sort set error, error info("
                    + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get str info set size by score from sort set
     * @param setName :
     * @param min :
     * @param max :
     * @return long :
     */
    public long getStrInfoSetSizeByScoreFromSortSet(String setName, double min, double max) {
        if (StringUtils.isEmpty(setName)) {
            LOGGER.error("Invaild param.");
            return 0L;
        }

        try {
            Long count = redisTemplate.opsForZSet().count(setName, min, max);
            return count == null ? 0L : count;
        } catch (Exception e) {
            LOGGER.error("Get str info size by score from sort set error, error info("
                    + e.getMessage() + ").");
        }
        return 0L;
    }

    @Override
    /**
     * get str info size from sort set
     * @param setName :
     * @return long :
     */
    public long getStrInfoSizeFromSortSet(String setName) {
        if (StringUtils.isEmpty(setName)) {
            LOGGER.error("Invaild param.");
            return 0L;
        }

        try {
            Long ret = redisTemplate.opsForZSet().size(setName);
            return ret == null ? 0L : ret;
        } catch (Exception e) {
            LOGGER.error("Get sort set size error, error info(" + e.getMessage() + ").");
        }
        return 0L;
    }

    @Override
    /**
     * scan str info from sort set
     * @param setName :
     * @param pattern :
     * @param count :
     * @return java.util.List<java.lang.String> :
     */
    public List<String> scanStrInfoFromSortSet(String setName, String pattern, long count) {
        if (StringUtils.isEmpty(setName) || StringUtils.isEmpty(pattern)) {
            LOGGER.error("Invaild param.");
            return null;
        }
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(count).build();
        try (Cursor<DefaultTypedTuple<String>> curosr =
                     redisTemplate.opsForZSet().scan(setName, scanOptions)) {

            List<String> valueList = new ArrayList<>();
            while (curosr.hasNext()) {
                valueList.add(curosr.next().getValue());
            }
            return valueList;
        } catch (Exception e) {
            LOGGER.error("Scan str info from sort set error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * get keys
     * @param pattern :
     * @return java.util.Set<java.lang.String> :
     */
    public Set<String> getKeys(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            LOGGER.error("Invaild param.");
            return null;
        }

        try {
            Set<String> keySet = redisTemplate.keys(pattern);
            return keySet;
        } catch (Exception e) {
            LOGGER.error("Get keys error, error info(" + e.getMessage() + ").");
        }
        return null;
    }

    @Override
    /**
     * delete key
     * @param key :
     * @return boolean :
     */
    public boolean deleteKey(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            LOGGER.error("Delete key error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * expire key
     * @param key :
     * @param timeoutSeconds :
     * @return boolean :
     */
    public boolean expireKey(String key, long timeoutSeconds) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LOGGER.error("Expire key error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * expire key at
     * @param key :
     * @param expiredTime :
     * @return boolean :
     */
    public boolean expireKeyAt(String key, long expiredTime) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            redisTemplate.expireAt(key, new Date(expiredTime));
            return true;
        } catch (Exception e) {
            LOGGER.error("Expire key error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    @Override
    /**
     * get expire
     * @param key :
     * @return long :
     */
    public long getExpire(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return -1L;
        }

        try {
            Long ret = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ret == null ? -1L : ret;
        } catch (Exception e) {
            LOGGER.error("Expire key error, error info(" + e.getMessage() + ").");
        }
        return -1L;
    }

    @Override
    /**
     * exists
     * @param key :
     * @return boolean :
     */
    public boolean exists(String key) {
        if (StringUtils.isEmpty(key)) {
            LOGGER.error("Invaild param.");
            return false;
        }

        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            LOGGER.error("Exist key error, error info(" + e.getMessage() + ").");
        }
        return false;
    }

    /**
     * get current time
     *
     * @return : long
     */
    public long getCurrentTime() {
        Long ret = (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.time();
            }
        });
        return ret == null ? 0L : ret;
    }

    /**
     * object to map
     *
     * @param obj :
     * @param <T> :
     * @return Map<String , String> :
     * @throws Exception :
     */
    public <T> Map<String, String> objectToHashMap(T obj) throws Exception {

        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Map<String, String> hashMap = new HashMap<String, String>(COLLECTION_INIT_SIZE);

        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName != null) {
                PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
                Method getMethod = pd.getReadMethod();

                Object itemObj = getMethod.invoke(obj, new Object[]{});

                Class type = field.getType();

                String key = fieldName;
                String value = null;
                if (Integer.class.equals(type) || int.class.equals(type)) {
                    value = String.valueOf(itemObj);
                } else if (Long.class.equals(type) || long.class.equals(type)) {
                    value = String.valueOf(itemObj);
                } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                    value = String.valueOf(itemObj);
                } else if (Date.class.equals(type)) {
                    value = String.valueOf(((Date) itemObj).getTime());
                } else if (Short.class.equals(type) || short.class.equals(type)) {
                    value = String.valueOf(itemObj);
                } else if (Float.class.equals(type) || float.class.equals(type)) {
                    value = String.valueOf(itemObj);
                } else if (Double.class.equals(type) || double.class.equals(type)) {
                    value = String.valueOf(itemObj);
                } else if (String.class.equals(type)) {
                    value = (String) itemObj;
                } else {
                    throw new Exception("Unsupport value type,field name("
                            + fieldName + "),type(" + type.getName() + ").");
                }

                if (value != null) {
                    hashMap.put(key, value);
                }
            }
        }
        return hashMap;
    }

    /**
     * map to object
     *
     * @param data :
     * @param <T>  :
     * @param cls  :
     * @return T :
     * @throws Exception :
     */
    public <T> T mapToObject(Map<String, String> data, Class<T> cls) throws Exception {

        Field[] fields = cls.getDeclaredFields();
        T retObj = cls.newInstance();
        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldValue = data.get(fieldName);
            if (fieldName != null && fieldValue != null) {
                PropertyDescriptor pd = new PropertyDescriptor(fieldName, cls);
                Method setMethod = pd.getWriteMethod();

                Class type = field.getType();
                if (Integer.class.equals(type) || int.class.equals(type)) {
                    Integer integerVal = Integer.valueOf(fieldValue);
                    setMethod.invoke(retObj, integerVal);
                } else if (Long.class.equals(type) || long.class.equals(type)) {
                    Long longVal = Long.valueOf(fieldValue);
                    setMethod.invoke(retObj, longVal);
                } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                    Boolean boolVal = Boolean.getBoolean(fieldValue);
                    setMethod.invoke(retObj, boolVal);
                } else if (Date.class.equals(type)) {
                    Long longVal = Long.valueOf(fieldValue);
                    Date date = new Date(longVal);
                    setMethod.invoke(retObj, date);
                } else if (Short.class.equals(type) || short.class.equals(type)) {
                    setMethod.invoke(retObj, Short.valueOf(fieldValue));
                } else if (Float.class.equals(type) || float.class.equals(type)) {
                    setMethod.invoke(retObj, Float.valueOf(fieldValue));
                } else if (Double.class.equals(type) || double.class.equals(type)) {
                    setMethod.invoke(retObj, Double.valueOf(fieldValue));
                } else if (String.class.equals(type)) {
                    setMethod.invoke(retObj, fieldValue);
                } else {
                    throw new Exception("Unsupport value type,field name(" + fieldName
                            + "),type(" + type.getName() + ").");
                }

            }
        }
        return retObj;
    }
}
