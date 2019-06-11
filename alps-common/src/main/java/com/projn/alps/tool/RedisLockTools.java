package com.projn.alps.tool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis lock tools
 *
 * @author : sunyuecheng
 */
@Component
public class RedisLockTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockTools.class);

    private static final String LOCK_KEY_HEADER = "l.k:%s";

    @Autowired
    protected RedisTemplate redisTemplate;

    /**
     * lock
     *
     * @param keyTail :
     * @param ttl     :
     * @return boolean :
     */
    public synchronized boolean lock(String keyTail, long ttl) {
        if (StringUtils.isEmpty(keyTail) || ttl < 0) {
            LOGGER.error("Error param.");
            return false;
        }
        boolean locked = false;
        String key = String.format(LOCK_KEY_HEADER, keyTail);
        try {
            Long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (time == null || time == -1L) {
                redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
                locked = true;
            } else {
                Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl, TimeUnit.SECONDS);
                if (success != null && success) {
                    locked = true;
                } else {
                    locked = false;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Create lock error, key({}), error info({}).", key, e.getMessage());
        }
        return locked;
    }

    /**
     * unlock
     *
     * @param keyTail :
     * @return boolean :
     */
    public synchronized boolean unlock(String keyTail) {
        if (StringUtils.isEmpty(keyTail)) {
            LOGGER.error("Error param.");
            return false;
        }
        boolean locked = false;
        String key = String.format(LOCK_KEY_HEADER, keyTail);
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            LOGGER.error("Delete lock error, key({}), error info({}).", key, e.getMessage());
        }
        return locked;
    }
}
