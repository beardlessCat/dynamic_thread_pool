package com.bigyj.pool.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "thread-pools")
public class ThreadPoolProperties {
    List<ThreadPoolConfig> executors = new ArrayList<>();

    @Override
    public String toString() {
        return "ThreadPoolProperties{" +
                "executors=" + executors +
                '}';
    }
}
