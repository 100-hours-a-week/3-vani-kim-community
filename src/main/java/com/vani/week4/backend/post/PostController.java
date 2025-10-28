package com.vani.week4.backend.post;

import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.post.dto.request.PostCreateRequest;
import com.vani.week4.backend.post.dto.request.PostUpdateRequest;
import com.vani.week4.backend.post.dto.response.PostDetailResponse;
import com.vani.week4.backend.post.dto.response.PostResponse;
import com.vani.week4.backend.post.dto.response.PostSummaryResponse;
import com.vani.week4.backend.global.dto.SliceResponse;
import com.vani.week4.backend.post.service.PostService;
import com.vani.week4.backend.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author vani
 * @since 10/8/25
 */
//TODO  1. 업데이트 된 새로운 게시글들로 돌아가는 api추가
//TODO  2. 게시글 검색 추가
//XXX 돌아갔을떄 다시 보여준거 보여줄지도 결정...
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    //게시글 목록 조회
    // TODO 매개변수 DTO처리, id, createdAt 하나만 오지 않게 검증
    @GetMapping
    public ResponseEntity<SliceResponse<PostSummaryResponse>> getPosts(
            @RequestParam(required = false) String cursorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
            ) {
        SliceResponse<PostSummaryResponse> response = postService.getPosts(cursorId,cursorCreatedAt, size);
        return ResponseEntity.ok(response);
    }

    //게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable("postId") String postId,
            @CurrentUser User user
    ){
        PostDetailResponse response = postService.getPostDetail(postId, user);
        return ResponseEntity.ok(response);
    }

    //게시글 생성
    @PostMapping()
    public ResponseEntity<PostDetailResponse> createPost(
            @CurrentUser User user,
            @Valid @RequestBody PostCreateRequest request ) {
        PostDetailResponse response = postService.createPost(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @CurrentUser User user,
            @PathVariable String postId,
            @Valid @RequestBody PostUpdateRequest request ) {
        PostDetailResponse response = postService.updatePost(user, postId, request);
        return ResponseEntity.ok(response);
    }

    //게시글 삭제
    //TODO softdelete 검토
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @CurrentUser User user,
            @PathVariable String postId
    ) {
        postService.deletePost(user, postId);
        return ResponseEntity.noContent().build();
    }

}
