package com.vani.week4.backend.loadtest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 부하 테스트 데이터 생성 API
 *
 * 사용법:
 * - POST /api/loadtest/generate - 모든 테스트 데이터 생성
 * - DELETE /api/loadtest/cleanup - 모든 테스트 데이터 삭제
 *
 * 주의: 운영 환경에서는 이 API를 비활성화하거나 접근 제어를 추가해야 합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/loadtest")
@RequiredArgsConstructor
public class LoadTestController {

    private final LoadTestDataGenerator dataGenerator;

    /**
     * 모든 부하 테스트 데이터 생성
     *
     * 생성되는 데이터:
     * - User: 10,000명
     * - Post: 1,000,000개
     * - Comment: 약 10,000,000개
     * - Like: 약 30,000,000개
     *
     * 예상 소요 시간: 수 분 ~ 수십 분 (시스템 성능에 따라 다름)
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateTestData() {
        log.info("부하 테스트 데이터 생성 API 호출됨");

        long startTime = System.currentTimeMillis();

        try {
            dataGenerator.generateAllData();

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000; // 초 단위

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "테스트 데이터 생성이 완료되었습니다.");
            response.put("duration_seconds", duration);
            response.put("data_counts", Map.of(
                "users", 10_000,
                "posts", 1_000_000,
                "comments", "약 10,000,000",
                "likes", "약 30,000,000"
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("테스트 데이터 생성 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "테스트 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 회원 데이터만 생성 (10,000명)
     */
    @PostMapping("/generate/users")
    public ResponseEntity<Map<String, Object>> generateUsers() {
        log.info("회원 데이터 생성 API 호출됨");

        try {
            var userIds = dataGenerator.generateUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원 데이터 생성이 완료되었습니다.");
            response.put("count", userIds.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("회원 데이터 생성 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "회원 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 테스트 데이터만 선택적으로 삭제 (기존 데이터 보존)
     *
     * 'lt'로 시작하는 닉네임을 가진 회원과 관련된 데이터만 삭제합니다.
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupTestData() {
        log.info("테스트 데이터 삭제 API 호출됨 (선택적 삭제)");

        try {
            dataGenerator.deleteAllTestData();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "테스트 데이터만 삭제되었습니다 (ltuser* 회원). 기존 데이터는 보존되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("테스트 데이터 삭제 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "테스트 데이터 삭제 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 모든 데이터를 강제로 삭제 (TRUNCATE 사용)
     *
     * 경고: 기존 데이터도 모두 삭제됩니다!
     */
    @DeleteMapping("/cleanup/all")
    public ResponseEntity<Map<String, Object>> truncateAllData() {
        log.warn("모든 데이터 삭제 API 호출됨 (TRUNCATE)");

        try {
            dataGenerator.truncateAllTables();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "모든 데이터가 삭제되었습니다 (TRUNCATE).");
            response.put("warning", "기존 데이터도 모두 삭제되었습니다!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("데이터 삭제 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "데이터 삭제 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 데이터 생성 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        // TODO: 실제로는 데이터 생성 진행 상태를 추적하는 로직 필요
        Map<String, Object> response = new HashMap<>();
        response.put("message", "부하 테스트 데이터 생성 API가 활성화되어 있습니다.");
        response.put("endpoints", Map.of(
            "generate", "POST /api/loadtest/generate - 모든 데이터 생성",
            "cleanup", "DELETE /api/loadtest/cleanup - 모든 데이터 삭제",
            "status", "GET /api/loadtest/status - 상태 확인"
        ));

        return ResponseEntity.ok(response);
    }
}