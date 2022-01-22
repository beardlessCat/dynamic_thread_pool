package com.bigyj.pool.hook;

import com.bigyj.pool.namager.ThreadPoolExecutorManager;

import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorsShutdownHook {
    public static void executorsShutdown(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            private volatile boolean hasShutdown = false;
            private AtomicInteger shutdownTimes = new AtomicInteger(0);

            @Override
            public void run() {
                ThreadPoolExecutorManager instance = ThreadPoolExecutorManager.getInstance();
                instance.getAll().keySet().stream().forEach(name->{
                    instance.getThreadPoolExecutor(name).shutdown();
                });
            }
        }, "ShutdownHook"));
    }
}
