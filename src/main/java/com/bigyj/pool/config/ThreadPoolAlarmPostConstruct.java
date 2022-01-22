package com.bigyj.pool.config;

import com.bigyj.pool.alarm.AlarmMetaData;
import com.bigyj.pool.alarm.ThreadPoolAlarm;
import com.bigyj.pool.executor.DynamicThreadPoolExecutor;
import com.bigyj.pool.namager.ThreadPoolExecutorManager;
import com.bigyj.pool.queue.ResizableCapacityLinkedBlockIngQueue;
import com.dianping.cat.status.StatusExtension;
import com.dianping.cat.status.StatusExtensionRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class ThreadPoolAlarmPostConstruct {
    private ThreadPoolProperties threadPoolProperties;
    private ThreadPoolAlarm threadPoolAlarm ;

    @Autowired
    public ThreadPoolAlarmPostConstruct(ThreadPoolProperties threadPoolProperties,ThreadPoolAlarm threadPoolAlarm) {
        this.threadPoolProperties = threadPoolProperties;
        this.threadPoolAlarm = threadPoolAlarm;
    }

    @PostConstruct
    public void init(){
        initAlarm();
    }

    private void initAlarm(){
        new Thread(()->{
            while (true){
                threadPoolProperties.getExecutors().stream().forEach(threadPoolConfig -> {
                    ConcurrentHashMap<String, DynamicThreadPoolExecutor> all = ThreadPoolExecutorManager.getInstance().getAll();
                    if(!all.keySet().isEmpty()){
                        DynamicThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorManager.getInstance().getThreadPoolExecutor(threadPoolConfig.getName());
                        ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) threadPoolExecutor.getQueue();
                        //状态上报至cat，记录当前线程状态信息
                        this.registerStatusExtension(threadPoolConfig, threadPoolExecutor);
                        int queueCapacityThreshold = threadPoolConfig.getQueueCapacityThreshold();
                        //判断线程队列使用量是否超过阈值
                        int taskCount = queue.size();
                        if (taskCount > queueCapacityThreshold) {
                            AlarmMetaData alarmMetaData = new AlarmMetaData() ;
                            alarmMetaData.setType("1");
                            alarmMetaData.setContent(threadPoolConfig.getName()+"队列使用数量已经达到阈值："+queueCapacityThreshold);
                            //线程队列使用量超过阈值
                            threadPoolAlarm.alarm(alarmMetaData);
                        }
                        //任务拒绝数量
                        AtomicLong rejectCount = ThreadPoolExecutorManager.getInstance().getRejectedCount(threadPoolConfig.getName());
                        if (rejectCount != null && rejectCount.get() > 0) {
                            //出现拒绝任务情况，进行预警通知
                            AlarmMetaData alarmMetaData = new AlarmMetaData() ;
                            alarmMetaData.setType("1");
                            alarmMetaData.setContent(threadPoolConfig.getName()+"出现拒绝任务时间，拒绝任务数量为："+rejectCount.get());
                            //线程队列使用量超过阈值
                            threadPoolAlarm.alarm(alarmMetaData);
                        }
                    }
                });
                //增加休眠时间，防止空转
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void registerStatusExtension(ThreadPoolConfig threadPoolConfig, DynamicThreadPoolExecutor executor) {
        StatusExtensionRegister.getInstance().register(new StatusExtension() {
            @Override
            public String getId() {
                return "thread.pool.info." + threadPoolConfig.getName();
            }

            @Override
            public String getDescription() {
                return "线程池监控";
            }

            @Override
            public Map<String, String> getProperties() {
                AtomicLong rejectCount = ThreadPoolExecutorManager.getInstance().getRejectedCount(threadPoolConfig.getName());
                Map<String, String> pool = new HashMap<>();
                pool.put("queueRemainingCapacity", String.valueOf(executor.getQueue().remainingCapacity()));
                pool.put("activeCount", String.valueOf(executor.getActiveCount()));
                pool.put("largestPoolSize", String.valueOf(executor.getLargestPoolSize()));
                pool.put("taskCount", String.valueOf(executor.getTaskCount()));
                pool.put("rejectCount", String.valueOf(rejectCount == null ? 0 : rejectCount.get()));
                pool.put("waitTaskCount", String.valueOf(executor.getQueue().size()));
                return pool;
            }
        });
    }

    private static String divide(int nunOne,int numTwo){
        return String.format("%1.2f%%",Double.parseDouble(nunOne+"")/Double.parseDouble(numTwo+"")*100);
    }
}
