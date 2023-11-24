package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import jdk.vm.ci.meta.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleLockImpl implements ILock{
    private static final String KEY_PREFIX = "lock:";
    private static final String ID_PREFIX = UUID.randomUUID().toString() + "-";
    private StringRedisTemplate redisTemplate;
    private String name;

    public SimpleLockImpl(StringRedisTemplate redisTemplate, String name) {
        this.redisTemplate = redisTemplate;
        this.name = name;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        String key = KEY_PREFIX + name;
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, threadId, timeoutSec, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(success);
    }

    @Override
    public void unlock() {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        String key = KEY_PREFIX + name;
        String id = redisTemplate.opsForValue().get(key);
        if (id.equals(threadId)) {
            redisTemplate.delete(key);
        }
    }
}
