package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;


    /**
     * Redis save
     * @param key String key,
     * @param value String value,
     * @param timeout timeout duration,
     * @param timeUnit TimeUnit, Enum
     * @since 1.0.0
     * @apiNote if timeout = 5,timeUnit = TimeUnit.HOURES -> 5시간 유효
     * */
    public void save(String key, String value, long timeout, TimeUnit timeUnit) {
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        valueOps.set(key, value, timeout, timeUnit);
    }

    public String find(String key) {
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        return valueOps.get(key);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}
