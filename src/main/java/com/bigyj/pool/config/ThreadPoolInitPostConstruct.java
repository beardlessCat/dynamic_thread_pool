package com.bigyj.pool.config;

import com.bigyj.pool.executor.DynamicThreadPoolExecutor;
import com.bigyj.pool.factory.RejectedExecutionHandlerFactory;
import com.bigyj.pool.hook.ExecutorsShutdownHook;
import com.bigyj.pool.namager.ThreadPoolExecutorManager;
import com.bigyj.pool.queue.ResizableCapacityLinkedBlockIngQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class ThreadPoolInitPostConstruct {
    private ThreadPoolProperties threadPoolProperties ;
    @Autowired
    public ThreadPoolInitPostConstruct(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }
    @PostConstruct
    public void init() {
        log.info(threadPoolProperties.toString());
        log.info("开始加载线程池配置....");
        createThreadPoolExecutor(threadPoolProperties);
        log.info("线程池配置加载完成....");
        //注册jvm退出钩子函数,jvm退出前停止线程池
        ExecutorsShutdownHook.executorsShutdown();
    }

    private void createThreadPoolExecutor(ThreadPoolProperties threadPoolProperties) {
        List<ThreadPoolConfig> executors = threadPoolProperties.getExecutors();
        executors.stream().forEach(threadPoolConfig -> {
            DynamicThreadPoolExecutor threadPoolExecutor = new DynamicThreadPoolExecutor(
                    threadPoolConfig.getCorePoolSize(),
                    threadPoolConfig.getMaxPoolSize(),
                    threadPoolConfig.getKeepAliveTime(),
                    threadPoolConfig.getUnit(),
                    new ResizableCapacityLinkedBlockIngQueue<>(threadPoolConfig.getQueueCapacity()),
                    (r) -> {
                        Thread thread = new Thread(r, threadPoolConfig.getThreadName());
                        return thread;
                    },
                    RejectedExecutionHandlerFactory.getRejectedExecutionHandler(threadPoolConfig.getRejectedExecutionType(),threadPoolConfig.getName()),
                    threadPoolConfig.getName());
            //线程池无任务是否销毁核心线程
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            ThreadPoolExecutorManager.getInstance().addThreadPoolExecutor(threadPoolConfig.getName(),threadPoolExecutor);
        });
        log.info("线程池初始化完成：{}",ThreadPoolExecutorManager.getInstance().getAll());
    }

    public void refreshThreadPoolExecutor() {
        List<ThreadPoolConfig> executors = threadPoolProperties.getExecutors();
        executors.stream().forEach(threadPoolConfig -> {
            DynamicThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorManager.getInstance().getThreadPoolExecutor(threadPoolConfig.getName());
            threadPoolExecutor.setCorePoolSize(5);
            threadPoolExecutor.setMaximumPoolSize(10);
            ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) threadPoolExecutor.getQueue();
            queue.setCapacity(10);
            log.info("线程池{}核心配置刷新完成。",threadPoolConfig.getName());
        });
    }
}
