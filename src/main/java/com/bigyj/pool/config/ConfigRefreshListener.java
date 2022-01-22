package com.bigyj.pool.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigRefreshListener {
    @Autowired
    private ThreadPoolInitPostConstruct threadPoolInitPostConstruct;
    /**
     * 监听Nacos加载
     *
     * @param config
     */
    @NacosConfigListener(dataId = "POOL_CONFIG", type = ConfigType.YAML)
    public void onMessage(String config) {
        threadPoolInitPostConstruct.refreshThreadPoolExecutor();
    }
}
