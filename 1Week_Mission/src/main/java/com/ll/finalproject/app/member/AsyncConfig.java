package com.ll.finalproject.app.member;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 *  이메일 전송 비동기 동작
 */
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 기본적으로 실행 대기 중인 Thread 개수
        executor.setMaxPoolSize(10); // 동시에 동작하는 최대 Thread 개수
        executor.setQueueCapacity(500); // 스레스 생성 대기 용량, [대기 용량 초과하면 예외 발생]
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}