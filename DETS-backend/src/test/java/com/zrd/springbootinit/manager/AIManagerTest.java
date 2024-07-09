package com.zrd.springbootinit.manager;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AIManagerTest {

    @Resource
    private AIManager aiManager;
    //@Test
    void doChat() {
        String doChat = aiManager.doChat("\n" +
                "分析需求：\n" +
                "分析网站用户的增长情况\n" +
                "原始数据：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                "2号,20\n" +
                "3号,30");
        String[] split = doChat.split("【【【【【");
        String substring = split[1].substring(13);
        System.out.println(substring);
    }
}