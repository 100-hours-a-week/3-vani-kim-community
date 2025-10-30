package com.vani.week4.backend.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vani
 * @since 10/28/25
 */
@Getter
@Setter
public class SessionData {

    private String userId;
    private long lastAccessedTime;

    public SessionData(String userId){
        this.userId = userId;
        this.lastAccessedTime = System.currentTimeMillis();
    }

    // 마지막 접근 시간을 갱신하는 메서드
    public void updateLastAccessedTime(){
        this.lastAccessedTime = System.currentTimeMillis();
    }
}
