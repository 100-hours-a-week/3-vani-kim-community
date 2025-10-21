package com.vani.week4.backend.post.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import com.vani.week4.backend.global.dto.SliceResponse;
import com.vani.week4.backend.post.dto.request.PostCreateRequest;
import com.vani.week4.backend.post.dto.response.PostResponse;
import com.vani.week4.backend.post.dto.response.PostSummaryResponse;
import com.vani.week4.backend.post.entity.Post;
import com.vani.week4.backend.post.entity.PostContent;
import com.vani.week4.backend.post.repository.PostRepository;
import com.vani.week4.backend.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.repository.query.Param;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
/**
 * @author vani
 * @since 10/21/25
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    // 주입 받을 목객체
    @Mock
    PostRepository postRepository;

    // 목 객체 주입 시킬곳
    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        //given
        String userId ="UTID111";
        String nickname = "Perplexity";
        String profileImageKey = null;

        User user = User.createUser(
                userId,
                nickname,
                profileImageKey
        );

        String postId = "PTID111";
        String title = "ㄷㄷ aws 터짐";

        //결과와 비교할 게시글, 게시글 내용 객체
        Post post = Post.builder()
                .id(postId)
                .user(user)
                .title(title)
                .build();

        String content = HtmlUtils.htmlEscape("이게 머선 일이고");
        String postImageKey = "postImageKey";

        PostContent postContent = PostContent.builder()
                .post(post)
                .content(content)
                .postImageKey(postImageKey)
                .build();

        PostCreateRequest request = new PostCreateRequest(title, content, postImageKey);

        //mock동작 설정
        when(postRepository.save(any(Post.class))).thenReturn(post);
        //when

        PostResponse result = postService.createPost(user, request);

        //Then
        assertThat(result).isNotNull();
        //ULID니까 존재 여부만 확인
        assertThat(result.postId()).isNotNull();
        assertThat(result.postId()).isNotEmpty();
        assertThat(result.title()).isEqualTo(title);
        assertThat(result.author().name()).isEqualTo(nickname);
        assertThat(result.contentDetail().content()).isEqualTo(content);
        assertThat(result.contentDetail().postImageKey()).isEqualTo(postImageKey);
    }

    @Test
    @DisplayName("다음 커서가 잘 생성되는지 확인")
    void getPosts_RequestSizePlusOne() {
        // 아래에 하려는건 쿼리가 잘되냐인데 이건 의미가 없다. 이건 레포지토리의 문제이고
        // 해야하는건 서비스 로직이 올바를가를 확인 하는건데..
        // 서비스로직에서 하는걸 테스트하기
//        //given
//        User user = User.createUser("user1", "uuuser", null);
//        List<Post> posts = new ArrayList<>();
//        Pageable pageable = PageRequest.of(0, 20);
//        for(int i=0; i < 20; i++){
//            posts.add(
//                    Post.builder().id(UUID.randomUUID().toString()).user(user).title("title" + i).build()
//            );
//        }
//
//
//        when(postRepository.findByCursor(posts.get(0).getCreatedAt(), posts.get(0).getId(), pageable))
//                .thenReturn();
//    );
        //given
        User user = User.createUser("UTID111", "작성자", null);
        int size = 20;
        String cursorId = "qazw111";
        LocalDateTime cursorCreatedAt = LocalDateTime.now();
        List<Post> posts = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, size);

        for(int i=0; i < size; i++){
            posts.add(
                    Post.builder().id("id" + i).user(user).title("title" + i).build()
            );
        }
        // slice객체 주어진 리스트로 그대로 만들기
        Slice<Post> slice = new SliceImpl<>(posts, pageable, true);

        when(postRepository.findByCursor(anyString(), any(LocalDateTime.class), any(Pageable.class))).
                thenReturn(slice);
        // when
        SliceResponse<PostSummaryResponse> response = postService.getPosts(cursorId, cursorCreatedAt, size);

        // then
        assertThat(response).isNotNull();
        assertThat(response.hasMore()).isTrue();
        assertThat(response.nextCursor().id()).isEqualTo("id19");
        assertThat(response.items()).hasSize(size);
    }
}