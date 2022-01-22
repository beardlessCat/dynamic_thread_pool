package com.bigyj.pool;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NacosPropertySource(dataId = "POOL_CONFIG", autoRefreshed = true, type = ConfigType.YAML)
public class DynamicThreadPoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicThreadPoolApplication.class, args);
    }

}
