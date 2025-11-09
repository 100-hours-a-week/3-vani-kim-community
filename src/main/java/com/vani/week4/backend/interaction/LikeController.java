package com.vani.week4.backend.interaction;

import com.vani.week4.backend.global.CurrentUser;
import com.vani.week4.backend.interaction.service.LikeService;
import com.vani.week4.backend.user.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vani
 * @since 10/15/25
 */
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("api/v1/post/{postId}/likes")
public class LikeController {
    private final LikeService likeService;

    //게시글 좋아요 토글 시
    @PostMapping
    public ResponseEntity<Void> postLikeToggle(
            @CurrentUser User user,
            @PathVariable String postId) {
        likeService.toggleLike(user, postId);
        return ResponseEntity.ok().build();
    }
}
