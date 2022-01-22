package com.bigyj.pool.config;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class ThreadPoolConfig {
    /**
     * 线程池名称
     */
    private String name = "DEFAULT-POOL";

    /**
     * 核心线程数
     */
    private int corePoolSize = 1;

    /**
     * 最大线程数, 默认值为CPU核心数量
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 队列最大数量
     */
    private int queueCapacity = Integer.MAX_VALUE;

    /**
     * 队列类型
     */

    /**
     * SynchronousQueue 是否公平策略
     */
    private boolean fair;

    /**
     * 拒绝策略
     * @see
     */

    /**
     * 空闲线程存活时间
     */
    private long keepAliveTime;

    /**
     * 空闲线程存活时间单位
     */
    private TimeUnit unit = TimeUnit.MILLISECONDS;

    /**
     * 队列容量阀值，超过此值告警
     */
    private int queueCapacityThreshold = queueCapacity;
    /**
     * 线程名称
     */
    private String threadName ;
    /**
     * 拒绝策略
     */
    private String rejectedExecutionType ;
}
