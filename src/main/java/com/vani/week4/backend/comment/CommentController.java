package com.vani.week4.backend.comment;

import com.vani.week4.backend.comment.dto.CommentCreateRequest;
import com.vani.week4.backend.comment.dto.CommentResponse;
import com.vani.week4.backend.comment.dto.CommentUpdateRequest;
import com.vani.week4.backend.comment.dto.CommentUpdateResponse;
import com.vani.week4.backend.comment.service.CommentService;
import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.global.dto.SliceResponse;
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
 * 댓글 생성, 조회를 담당하는 컨트롤러
 * @author vani
 * @since 10/8/25
 */
//TODO 댓글 추천(좋아요), 댓글 정렬(추천,시간)
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    //TODO 요청 dto,validator 작성,
    //TODO 댓글 읽기 전략 세분화 한번에 같은 그룹에서 몇개까지 등
    //TODO 댓글 정렬기능, 댓글 추천 기능
    @GetMapping
    public ResponseEntity<SliceResponse<CommentResponse>> getComments(
            @PathVariable String postId,
            @RequestParam(required = false) String cursorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ){
        SliceResponse<CommentResponse> response = commentService.getComments(postId, cursorId, cursorCreatedAt, size);
        return ResponseEntity.ok(response);
    }

    //댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable String postId,
            @CurrentUser User user,
            @Valid @RequestBody CommentCreateRequest request){

        CommentResponse response = commentService.createComment(postId, user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @CurrentUser User user,
            @Valid @RequestBody CommentUpdateRequest request ) {

        CommentUpdateResponse response = commentService.updateComment(postId, commentId, user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //댓글 삭제
    //PATCH메서드가 2개일수 없어서 Delete
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @CurrentUser User user ){
        commentService.deleteComment(postId, commentId, user);
        return ResponseEntity.noContent().build();
    }



}
