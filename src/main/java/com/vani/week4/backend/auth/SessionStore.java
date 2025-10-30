package com.vani.week4.backend.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 저장소
 * @author vani
 * @since 10/28/25
 */
//싱글톤
@Component
public class SessionStore {
    // 세션 저장소 (Key : 세션 ID, Value : 사용자 ID)
    // 읽기 부하가 많으니까 Concurrent
    // 쓰기 부하와 읽기 부하 모두 많으니까..락 걸리는 HashMap은 기각
    private final Map<String, SessionData> sessionStore = new ConcurrentHashMap<>();

    // 세션 아이디 저장
    public void saveSessionId(String sessionId, String userId) {
        sessionStore.put(sessionId, new SessionData(userId));
    }

    // 세션 아이디 확인
    public String getUserIdBySessionId(String sessionId) {
        SessionData data = sessionStore.get(sessionId);
        if (data != null) {
            data.updateLastAccessedTime();
            return data.getUserId();
        }
        return null;
    }

    // 만료된 세션아이디 제거
    public void cleanupExpiredSessionId() {
        long now = System.currentTimeMillis();
        long timeoutMs = 30*60*1000; // 30분, 밀리초

        sessionStore.entrySet().removeIf(entry -> {
            long lastAccessedTime = entry.getValue().getLastAccessedTime();
            return (now - lastAccessedTime) > timeoutMs;        // 30분 이상 지난건 삭제
        });
    }

    //세션 Id 직접 제거
    public void removeSessionId(String sessionId) {
        sessionStore.remove(sessionId);
    }
}
