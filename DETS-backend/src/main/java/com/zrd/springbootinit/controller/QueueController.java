package com.zrd.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程工作接口
 *
 * @author zrd
  
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","local"})
public class QueueController {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * 添加任务到线程池
     * @param name
     */
    @GetMapping("/add")
    public void add(String name)
    {
        CompletableFuture.runAsync(()->{
            System.out.println("任务执行中" + name + "线程执行人" + Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get()
    {
        HashMap<String, Object> map = new HashMap<>();

        //队列长度
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度",size);

        //当前正在执行的任务数量
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在执行任务数",activeCount);

        //线程池接受到的所有任务数量,包括失败的
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("所有任务数量",taskCount);

        //已完成的任务数量
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数量",completedTaskCount);

        return JSONUtil.toJsonStr(map);
    }

}
