package com.vani.week4.backend.auth;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author vani
 * @since 10/28/25
 */
@Component
@RequiredArgsConstructor
public class SessionCleanupScheduler {
    private final SessionStore sessionStore;

    // 자바 스케쥴러
    private ScheduledExecutorService scheduler;

    // 이 빈이 생성된 직후 시작
    @PostConstruct
    public void startScheduler(){
        //백그라운드에서 돌릴 스레드 1개 짜리 스케쥴러
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // 10분마다 작업 반복
        this.scheduler.scheduleAtFixedRate(
                sessionStore::cleanupExpiredSessionId,
                10,         //최초 지연시간, 시작하고 10분 후
                10,                   //반복 간견
                TimeUnit.MINUTES      //시간 단위
        );
    }

    // 앱이 종료되기 직전 "종료"
    // 지금하던 일까지 하고 새로운 일 받지 말고 종료합니다.
    @PreDestroy
    public void stopScheduler(){
        if(scheduler != null){
            scheduler.shutdown();
        }
    }
}
