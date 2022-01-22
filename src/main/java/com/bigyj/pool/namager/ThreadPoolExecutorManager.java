package com.bigyj.pool.namager;

import com.bigyj.pool.executor.DynamicThreadPoolExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池管理器
 */
public class ThreadPoolExecutorManager {
    /**
     * 单例模式创建对象
     */
    private volatile static  ThreadPoolExecutorManager instance ;
    private ThreadPoolExecutorManager(){}
    public static ThreadPoolExecutorManager getInstance(){
        if (instance == null) {
            synchronized (ThreadPoolExecutorManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolExecutorManager();
                }
            }
        }
        return instance;
    }


    private volatile ConcurrentHashMap<String, DynamicThreadPoolExecutor> executorServices = new ConcurrentHashMap<>();

    private volatile ConcurrentHashMap<String, AtomicLong> poolRejectedCounts = new ConcurrentHashMap<>();

    /**
     * 增加线程池
     */
    public void addThreadPoolExecutor(String name ,DynamicThreadPoolExecutor threadPoolExecutor){
        executorServices.putIfAbsent(name,threadPoolExecutor);
    }
    /**
     * 获取线程池对象
     */
    public DynamicThreadPoolExecutor getThreadPoolExecutor(String name ){
        return executorServices.get(name);
    }

    /**
     * 移除线程池
     * @param name
     */
    public void removeThreadPoolExecutor(String name){
        executorServices.remove(name);
    }

    public ConcurrentHashMap<String, DynamicThreadPoolExecutor>  getAll(){
        return executorServices;
    }

    public void putRejected(String name){


        AtomicLong atomicLong = poolRejectedCounts.putIfAbsent(name, new AtomicLong(1));
        if(atomicLong !=null){
            atomicLong.incrementAndGet();
        }
    }

    public AtomicLong getRejectedCount(String name){
        AtomicLong atomicLong = poolRejectedCounts.get(name);
        return atomicLong;
    }

}
