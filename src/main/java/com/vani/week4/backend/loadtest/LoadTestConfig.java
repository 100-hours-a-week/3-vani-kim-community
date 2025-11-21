package com.vani.week4.backend.loadtest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 부하 테스트 데이터 생성 설정
 *
 * application.yml 또는 application-loadtest.yml에서 설정값을 조정할 수 있습니다:
 *
 * loadtest:
 *   data:
 *     user-count: 10000
 *     post-count: 1000000
 *     avg-comments-per-post: 10
 *     avg-likes-per-post: 30
 *     batch-size: 1000
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "loadtest.data")
public class LoadTestConfig {

    /**
     * 생성할 회원 수
     * 기본값: 10,000
     */
    private int userCount = 100;

    /**
     * 생성할 게시글 수
     * 기본값: 1,000,000
     */
    private int postCount = 1_000;

    /**
     * 게시글당 평균 댓글 개수
     * 기본값: 10
     */
    private int avgCommentsPerPost = 1;

    /**
     * 게시글당 평균 좋아요 개수
     * 기본값: 30
     */
    private int avgLikesPerPost = 3;

    /**
     * 배치 삽입 크기
     * 기본값: 1000
     */
    private int batchSize = 100;

    /**
     * 데이터 생성 시작일 (과거 N일 전부터)
     * 기본값: 365 (1년 전)
     */
    private int dataPastDays = 365;

    // 계산된 값들

    public long getTotalComments() {
        return (long) postCount * avgCommentsPerPost;
    }

    public long getTotalLikes() {
        return (long) postCount * avgLikesPerPost;
    }

    public void printConfiguration() {
        System.out.println("=== 부하 테스트 데이터 생성 설정 ===");
        System.out.println("회원 수: " + String.format("%,d", userCount));
        System.out.println("게시글 수: " + String.format("%,d", postCount));
        System.out.println("평균 댓글/게시글: " + avgCommentsPerPost);
        System.out.println("평균 좋아요/게시글: " + avgLikesPerPost);
        System.out.println("예상 총 댓글 수: " + String.format("%,d", getTotalComments()));
        System.out.println("예상 총 좋아요 수: " + String.format("%,d", getTotalLikes()));
        System.out.println("배치 크기: " + batchSize);
        System.out.println("데이터 기간: 과거 " + dataPastDays + "일");
        System.out.println("===================================");
    }
}