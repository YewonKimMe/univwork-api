package net.univwork.api.api_v1.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() {
        valueOps = stringRedisTemplate.opsForValue();
    }

    @AfterEach
    void destroy() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @DisplayName("Redis Save Test")
    @Test
    void save() {
        String testKey = UUID.randomUUID().toString();
        String testValue = "abe@gmail.com";
        redisService.save(testKey, testValue, 10, TimeUnit.SECONDS);
        String findValue = redisService.find(testKey);

        assertThat(testValue).isEqualTo(findValue);
    }

    @DisplayName("Redis Find Test")
    @Test
    void find() {
        String key = "testKey";
        String value = "testValue";
        valueOps.set(key, value);

        String foundValue = redisService.find(key);
        assertThat(foundValue).isEqualTo(value);
    }

    @DisplayName("Redis Delete Test")
    @Test
    void delete() {
        String key = "testKey";
        String value = "testValue";
        valueOps.set(key, value);

        redisService.delete(key);
        String deletedValue = valueOps.get(key);
        assertThat(deletedValue).isNull();
    }
}