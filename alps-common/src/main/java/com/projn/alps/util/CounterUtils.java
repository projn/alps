package com.projn.alps.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.projn.alps.define.CommonDefine.MILLI_SECOND_1000;
import static com.projn.alps.define.CommonDefine.ONE_DAY_SECOND;

/**
 * counter utils
 *
 * @author : sunyuecheng
 */
public final class CounterUtils {
    private static long beginRecordTime = System.currentTimeMillis();

    private static Map<String, Double> counterMap = new ConcurrentHashMap<>();

    public static Map<String, Double> getCounterMap() {
        return counterMap;
    }

    /**
     * increase num
     *
     * @param key :
     */
    public static void increaseNum(String key) {
        if(System.currentTimeMillis() - beginRecordTime > ONE_DAY_SECOND * MILLI_SECOND_1000) {
            counterMap.clear();
            beginRecordTime = System.currentTimeMillis();
        }

        Double oldNum = counterMap.get(key);
        if (oldNum == null) {
            counterMap.put(key, 1d);
        } else {
            oldNum += 1;
            counterMap.put(key, oldNum);
        }
    }

    /**
     * increase num
     *
     * @param key :
     * @param num :
     */
    public static void increaseNum(String key, Double num) {
        if(System.currentTimeMillis() - beginRecordTime > ONE_DAY_SECOND * MILLI_SECOND_1000) {
            counterMap.clear();
            beginRecordTime = System.currentTimeMillis();
        }

        Double oldNum = counterMap.get(key);
        if (oldNum == null) {
            counterMap.put(key, num);
        } else {
            oldNum += num;
            counterMap.put(key, oldNum);
        }
    }

    /**
     * decrease num
     *
     * @param key :
     */
    public static void decreaseNum(String key) {
        if(System.currentTimeMillis() - beginRecordTime > ONE_DAY_SECOND * MILLI_SECOND_1000) {
            counterMap.clear();
            beginRecordTime = System.currentTimeMillis();
        }

        Double oldNum = counterMap.get(key);
        if (oldNum == null) {
            counterMap.put(key, 0d);
        } else {
            oldNum -= 1;
            counterMap.put(key, oldNum);
        }
    }

    /**
     * decrease num
     *
     * @param key :
     * @param num :
     */
    public static void decreaseNum(String key, Double num) {
        if(System.currentTimeMillis() - beginRecordTime > ONE_DAY_SECOND * MILLI_SECOND_1000) {
            counterMap.clear();
            beginRecordTime = System.currentTimeMillis();
        }

        Double oldNum = counterMap.get(key);
        if (oldNum == null) {
            counterMap.put(key, num);
        } else {
            oldNum -= num;
            counterMap.put(key, oldNum);
        }
    }

    /**
     * record max num
     *
     * @param key :
     * @param num :
     */
    public static void recordMaxNum(String key, Double num) {
        if(System.currentTimeMillis() - beginRecordTime > ONE_DAY_SECOND * MILLI_SECOND_1000) {
            counterMap.clear();
            beginRecordTime = System.currentTimeMillis();
        }

        Double oldNum = counterMap.get(key);
        if (oldNum == null) {
            counterMap.put(key, num);
        } else {
            if (oldNum < num) {
                counterMap.put(key, num);
            }
        }
    }

    /**
     * record min num
     *
     * @param key :
     * @param num :
     */
    public static void recordMinNum(String key, Double num) {
        if(System.currentTimeMillis() - beginRecordTime > ONE_DAY_SECOND * MILLI_SECOND_1000) {
            counterMap.clear();
            beginRecordTime = System.currentTimeMillis();
        }

        Double oldNum = counterMap.get(key);
        if (oldNum == null) {
            counterMap.put(key, num);
        } else {
            if (oldNum > num) {
                counterMap.put(key, num);
            }
        }
    }

    private CounterUtils() {
    }
}
