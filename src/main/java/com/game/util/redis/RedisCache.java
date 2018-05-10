package com.game.util.redis;

import com.game.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeniss on 18/4/19.
 */
@Repository
public class RedisCache {
    private final static Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private static final int LIVE_TIME = 86400;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    public void delKey(String key) {
        if (StringUtil.isEmpty(key)) {
            return;
        }
        List keys = new ArrayList<>();
        keys.add(key);
        delKeys(keys);
    }

    public void delKeys(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        redisTemplate.delete(keys);
    }

    /**
     * Set sadd
     * @param key
     * @param member
     */
    public void sadd(String key, String member) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add(key, member);

    }

    public boolean sisMember(String key, String member) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        return setOperations.isMember(key, member);
    }
}
