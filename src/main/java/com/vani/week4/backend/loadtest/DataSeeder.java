package com.vani.week4.backend.loadtest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 부하 테스트 데이터 자동 생성
 *
 * 사용법:
 * java -jar app.jar --spring.profiles.active=data-gen
 *
 * 데이터 생성이 완료되면 자동으로 종료됩니다.
 */
@Slf4j
@Component
@Profile("data-gen")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final LoadTestDataGenerator dataGenerator;

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("데이터 생성 프로파일 활성화됨");
        log.info("========================================");

        try {
            // 데이터 생성 실행
            dataGenerator.generateAllData();

            log.info("========================================");
            log.info("모든 데이터 생성이 완료되었습니다.");
            log.info("애플리케이션을 종료합니다.");
            log.info("========================================");

            // 데이터 생성 완료 후 애플리케이션 종료
            System.exit(0);

        } catch (Exception e) {
            log.error("데이터 생성 중 오류 발생", e);
            log.error("애플리케이션을 종료합니다.");
            System.exit(1);
        }
    }
}