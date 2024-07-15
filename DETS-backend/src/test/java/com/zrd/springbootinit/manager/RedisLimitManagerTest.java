package com.zrd.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RedisLimitManagerTest {

    @Resource
    private RedisLimitManager redisLimitManager;
    @Test
    void doLimit() {
        String userKey = "1";
        for (int i = 0; i < 6; i++) {
            redisLimitManager.doLimit(userKey);
            System.out.println("成功");
        }
    }
}