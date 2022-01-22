package com.bigyj.pool.controller;

import com.bigyj.pool.namager.ThreadPoolExecutorManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/pool")
public class PoolController {
    @GetMapping("/task")
    public void task(){
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorManager.getInstance().getThreadPoolExecutor("couponPool");
        for (int i=0;i<15;i++){
            threadPoolExecutor.submit(()->{
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
