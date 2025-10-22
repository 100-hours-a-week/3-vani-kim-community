package com.vani.week4.backend.post.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.vani.week4.backend.global.ErrorCode;
import com.vani.week4.backend.global.exception.PostNotFoundException;
import com.vani.week4.backend.global.exception.UnauthorizedException;
import com.vani.week4.backend.post.dto.request.PostCreateRequest;
import com.vani.week4.backend.post.dto.request.PostUpdateRequest;
import com.vani.week4.backend.post.dto.response.PostDetailResponse;
import com.vani.week4.backend.post.dto.response.PostResponse;
import com.vani.week4.backend.post.dto.response.PostSummaryResponse;
import com.vani.week4.backend.global.dto.SliceResponse;
import com.vani.week4.backend.post.entity.Post;
import com.vani.week4.backend.post.entity.PostContent;
import com.vani.week4.backend.post.repository.PostRepository;
import com.vani.week4.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vani
 * @since 10/14/25
 */
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    /**
     * 게시글 목록 커서 페이징을 위한 메서드, 생성일자와 Id 기준으로 내림차순
     * @param cursorId : 커서 페이징을 위한 postId
     * @param cursorCreatedAt : 커서 페이징을 위한 생성일자
     * @param size : 요청을 통해 가져올 게시글 수
     * */
    public SliceResponse<PostSummaryResponse> getPosts(
            String cursorId,
            LocalDateTime cursorCreatedAt,
            int size
    ) {
        // 정렬된 post들 가져오기
        Pageable pageable = PageRequest.of(0, size);
        Slice<Post> posts = postRepository.findByCursor(cursorId, cursorCreatedAt, pageable);

        return convertToSliceResponse(posts);
    }

    /**
     * 응답 형태로 변환하는 메서드
     * */
    private SliceResponse<PostSummaryResponse> convertToSliceResponse(Slice<Post> posts) {
        // Entity -> DTO 변환
        List<PostSummaryResponse> content = posts.getContent().stream()
                .map(this::toPostSummaryResponse)
                .toList();

        SliceResponse.Cursor nextCursor = createNewNextCursor(posts);

        return new SliceResponse<>(content, nextCursor, posts.hasNext());
    }

    /**
     * 다음 커서 생성하는 메서드
     * */
    private SliceResponse.Cursor createNewNextCursor(Slice<Post> posts) {
        if (!posts.hasNext() || posts.getContent().isEmpty()) {
            return null;
        }
        Post lastPost = posts.getContent().get(posts.getContent().size() - 1);
        return new SliceResponse.Cursor(
                lastPost.getId(),
                lastPost.getCreatedAt()
        );
    }

    /**
     * 응답 DTO로 변환하는 메서드
     * */
    private PostSummaryResponse toPostSummaryResponse(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt(),
                new PostSummaryResponse.Author(
                        post.getUser().getNickname(),
                        post.getUser().getProfileImageKey()
                ),
                new PostSummaryResponse.Stats(
                        post.getLikeCount(),
                        post.getCommentCount(),
                        post.getViewCount()
                )
        );
    }

    /**
     * 게시글 id를 이용하여 게시글 상세 정보를 불러 오는 메서드
     * @param postId : 게시글 아이디
     * */
    public PostDetailResponse getPostDetail(String postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        //TODO Count 로직 개선 필요
        post.incrementViewCount();
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                new PostDetailResponse.ContentDetail(
                        post.getPostContent().getContent(),
                        post.getPostContent().getPostImageKey()
                ),
                new PostDetailResponse.Author(
                        post.getUser().getNickname()
                ),
                new PostDetailResponse.Stats(
                        post.getLikeCount(),
                        post.getCommentCount(),
                        post.getViewCount()
                )
            );
    }

    /**
     * 게시글을 생성하는 메서드
     * @param user : 토큰의 주체, 사용자
     * @param request : dto
     * */
    @Transactional
    public PostDetailResponse createPost(User user, PostCreateRequest request){

        Post post = Post.builder()
                .id(UlidCreator.getUlid().toString())
                .user(user)
                .title(request.title())
                .build();

        PostContent postContent = PostContent.builder()
                .post(post)
                .content(HtmlUtils.htmlEscape(request.content()))
                .postImageKey(request.postImageKey())
                .build();

        post.setPostContent(postContent);

        postRepository.save(post);

        return toPostDetailResponse(post);
    }

    /**
     * 게시글을 수정하는 메서드
     * @param user : 토큰의 주체, 사용자
     * @param postId : 수정하려는 게시글 아이디
     * @param request : dto
     * */
    @Transactional
    public PostDetailResponse updatePost(User user, String postId, PostUpdateRequest request) {
        // 게시글 조회
        Post post = postRepository.findByIdWithContent(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 권한 확인
        if (!post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        // 제목 수정 (null이 아닐 때만)
        if (request.title() != null) {
            post.updateTitle(request.title());
        }

        // 내용 수정 (null이 아닐 때만)
        PostContent postContent = post.getPostContent();
        if (request.content() != null) {
            postContent.updateContent(HtmlUtils.htmlEscape(request.content()));
        }
        if (request.postImageKey() != null) {
            postContent.updatePostImageKey(request.postImageKey());
        }

        post.updateModifiedDate();

        return toPostDetailResponse(post);
    }

    private PostDetailResponse toPostDetailResponse(Post post) {
        PostContent content = post.getPostContent();
        User user = post.getUser();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                new PostDetailResponse.ContentDetail(
                        post.getPostContent().getContent(),
                        post.getPostContent().getPostImageKey()
                ),
                new PostDetailResponse.Author(
                        post.getUser().getNickname()
                ),
                new PostDetailResponse.Stats(
                        post.getLikeCount(),
                        post.getCommentCount(),
                        post.getViewCount()
                )
        );
    }

    /**
     * 게시글을 삭제하는 메서드, 완전 삭제(postcontent, like, comment)
     * */
    @Transactional
    public void deletePost(User user, String postId) {
        Post post = postRepository.findByIdWithContent(postId)
                .orElseThrow(() -> new PostNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }

        postRepository.delete(post);

    }
}
