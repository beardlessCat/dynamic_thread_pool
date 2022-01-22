package com.bigyj.pool.rejected;

import com.bigyj.pool.namager.ThreadPoolExecutorManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
@Data
@Slf4j
@AllArgsConstructor
public class AbortRejectedPolicy implements RejectedExecutionHandler {
    private String threadPoolName ;

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        ThreadPoolExecutorManager.getInstance().putRejected(threadPoolName);
        log.error("任务拒绝，当前被拒绝总数：{}",ThreadPoolExecutorManager.getInstance().getRejectedCount(threadPoolName));
//        throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
    }
}
