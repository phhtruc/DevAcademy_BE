package com.devacademy.DevAcademy_BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // Số thread tối thiểu
        executor.setMaxPoolSize(5);  // Số thread tối đa
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("VideoUploader-");
        executor.initialize();
        return executor;
    }
}
