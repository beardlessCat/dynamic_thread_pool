package com.bigyj.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
@Slf4j
public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        dynamicModifyExecutor();
    }

    public static ThreadPoolExecutor buildThreadPoolExecutor(){
        return new ThreadPoolExecutor(
                2,
                5,
                60,
                TimeUnit.SECONDS,
                new ResizableCapacityLinkedBlockIngQueue<>(3),
                (r)->{
                    Thread thread = new Thread(r,"ACTION-TASK-");
                    return thread;
                },
                (r,e)->{
                    log.info("触发拒绝测，策略：Task {} rejected from {}" ,r.toString() ,e.toString());
                    refreshExecutor(e);
                    e.submit(r);
                });
    }

    public static void dynamicModifyExecutor() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = buildThreadPoolExecutor();
        for (int i=0;i<15;i++){
            threadPoolExecutor.submit(()->{
                threadPoolStatus(threadPoolExecutor,"创建任务");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public static void refreshExecutor(ThreadPoolExecutor threadPoolExecutor){
        threadPoolStatus(threadPoolExecutor,"==========改变之前==============");
        threadPoolExecutor.setCorePoolSize(5);
        threadPoolExecutor.setMaximumPoolSize(10);
        ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) threadPoolExecutor.getQueue();
        queue.setCapacity(10);
        threadPoolStatus(threadPoolExecutor,"==========改变之后============");
    }
    private static void threadPoolStatus(ThreadPoolExecutor threadPoolExecutor, String taskName) {
        ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) threadPoolExecutor.getQueue();
        log.info("{}-{}-:核心线程数：{} 活动线程数:{} 最大线程数:{} 线程活跃度:{} 任务完成数:{} 队列大小:{} 当前排队线程数:{} 队列剩余大小:{} 队列实用度:{}",
                Thread.currentThread().getName(),
                taskName,
                threadPoolExecutor.getCorePoolSize(),
                threadPoolExecutor.getActiveCount(),
                threadPoolExecutor.getMaximumPoolSize(),
                divide(threadPoolExecutor.getActiveCount(),threadPoolExecutor.getMaximumPoolSize()),
                threadPoolExecutor.getCompletedTaskCount(),
                (queue.size()+queue.remainingCapacity()),
                queue.size(),
                queue.remainingCapacity(),
                divide(queue.size(),queue.size()+queue.remainingCapacity())
        );
    }
    private static String divide(int nunOne,int numTwo){
        return String.format("%1.2f%%",Double.parseDouble(nunOne+"")/Double.parseDouble(numTwo+"")*100);
    }
}
