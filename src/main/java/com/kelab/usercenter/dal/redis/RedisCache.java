package com.kelab.usercenter.dal.redis;

import com.alibaba.fastjson.JSON;
import com.kelab.usercenter.config.AppSetting;
import com.kelab.usercenter.constant.enums.CacheConstant;
import com.kelab.usercenter.dal.redis.callback.ListCacheCallback;
import com.kelab.usercenter.dal.redis.callback.OneCacheCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class RedisCache {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public String get(CacheConstant bizName, String key) {
        return redisTemplate.opsForValue().get(bizName + key);
    }

    public void set(CacheConstant bizName, String key, String value) {
        try {
            redisTemplate.opsForValue().set(bizName + key, value, AppSetting.cacheMillisecond, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <K, V> List<V> cacheList(CacheConstant bizName, List<K> keys, Class<V> clazz, ListCacheCallback<K, V> callback) {
        List<String> cacheKeys = genCacheKeyList(bizName, keys);
        List<V> result = new ArrayList<>();
        try {
            List<K> missHitKeyList = new ArrayList<>();
            List<String> cacheList = redisTemplate.opsForValue().multiGet(cacheKeys);
            assert cacheList != null;
            for (int i = 0; i < cacheList.size(); i++) {
                String cacheObj = cacheList.get(i);
                // missed cache
                if (cacheObj == null) {
                    missHitKeyList.add(keys.get(i));
                } else {
                    result.add(JSON.parseObject(cacheObj, clazz));
                }
            }
            if (missHitKeyList.size() != 0) {
                // db query
                Map<K, V> dbObjMap = callback.queryFromDB(missHitKeyList);
                if (dbObjMap != null) {
                    Map<String, String> cacheMap = new HashMap<>();
                    dbObjMap.forEach((k, v) -> {
                        cacheMap.put(bizName + k.toString(), JSON.toJSONString(v));
                        result.add(v);
                    });
                    redisTemplate.opsForValue().multiSet(cacheMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public <K, V> V cacheOne(CacheConstant bizName, K key, Class<V> clazz, OneCacheCallback<K, V> callback) {
        String cacheObj = redisTemplate.opsForValue().get(bizName + key.toString());
        // missed cache
        if (cacheObj == null) {
            V dbObj = callback.queryFromDB(key);
            // missed db
            if (dbObj == null) {
                return null;
            } else {
                redisTemplate.opsForValue().set(bizName + key.toString(), JSON.toJSONString(dbObj), AppSetting.cacheMillisecond, TimeUnit.MILLISECONDS);
                return dbObj;
            }
        } else {
            return JSON.parseObject(cacheObj, clazz);
        }
    }

    private List<String> genCacheKeyList(CacheConstant bizName, List<?> keys) {
        List<String> cacheKeys = new ArrayList<>(keys.size());
        keys.forEach(item -> cacheKeys.add(bizName + item.toString()));
        return cacheKeys;
    }

    public Object getAndSet(CacheConstant bizName, String key, String value) {
        String result;
        try {
            result = redisTemplate.opsForValue().getAndSet(bizName + key, value);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    public boolean delete(CacheConstant bizName, Object key) {
        boolean result = false;
        try {
            redisTemplate.delete(bizName + key.toString());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteList(CacheConstant bizName, List<?> key) {
        boolean result = false;
        try {
            List<String> keys = new ArrayList<>();
            key.forEach(item -> keys.add(bizName + item.toString()));
            redisTemplate.delete(keys);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void zAdd(CacheConstant bizName, String zSetName, String value, Double score) {
        try {
            redisTemplate.opsForZSet().add(bizName + zSetName, value, score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}