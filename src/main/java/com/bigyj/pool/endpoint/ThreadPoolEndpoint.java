package com.bigyj.pool.endpoint;

import com.bigyj.pool.config.ThreadPoolProperties;
import com.bigyj.pool.executor.DynamicThreadPoolExecutor;
import com.bigyj.pool.namager.ThreadPoolExecutorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
@Component
@Endpoint(id = "thread-pool")
public class ThreadPoolEndpoint {
    @Autowired
    private ThreadPoolProperties threadPoolProperties ;
    @ReadOperation
    public Map<String, Object> threadPools() {
        Map<String, Object> data = new HashMap<>();

        List<Map> threadPools = new ArrayList<>();
        ConcurrentHashMap<String, DynamicThreadPoolExecutor> all = ThreadPoolExecutorManager.getInstance().getAll();
        for(String key:all.keySet()) {
            ThreadPoolExecutor executor = all.get(key);
            Map<String, Object> pool = new HashMap<>();
            pool.put("coreSize", executor.getCorePoolSize());
            pool.put("maxSize", executor.getMaximumPoolSize());
            pool.put("largestPoolSize", executor.getLargestPoolSize());
            pool.put("queueSize", executor.getQueue().size()+executor.getQueue().remainingCapacity());
            threadPools.add(pool);
        }
        data.put("threadPools", threadPools);
        return data;
    }

}
