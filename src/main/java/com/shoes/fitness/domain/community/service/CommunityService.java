package com.shoes.fitness.domain.community.service;

import com.shoes.fitness.domain.community.dto.*;
import com.shoes.fitness.domain.community.repository.*;
import com.shoes.fitness.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityPostImageRepository postImageRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityLikeRepository likeRepository;
    private final CommunityPartnerRepository partnerRepository;

    // ==================== 게시글 목록 조회 ====================

    public PostListResponse getPosts(String category, String status, String location, String keyword, int page, int size) {
        CommunityPost.PostCategory postCategory = null;
        if (category != null && !category.isEmpty()) {
            postCategory = CommunityPost.PostCategory.valueOf(category);
        }

        CommunityPost.TradeStatus tradeStatus = null;
        if (status != null && !status.isEmpty()) {
            tradeStatus = CommunityPost.TradeStatus.valueOf(status);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityPost> postsPage = postRepository.findByFilters(postCategory, tradeStatus, location, keyword, pageable);
        long totalCount = postRepository.countByFilters(postCategory, tradeStatus, location, keyword);

        List<PostResponse> postResponses = postsPage.getContent().stream()
                .map(post -> {
                    List<CommunityPostImage> images = postImageRepository.findByPostIdOrderBySortOrderAsc(post.getPostId());
                    String thumbnailUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
                    return PostResponse.fromWithThumbnail(post, thumbnailUrl);
                })
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .posts(postResponses)
                .totalCount(totalCount)
                .totalPages(postsPage.getTotalPages())
                .currentPage(page)
                .build();
    }

    // ==================== 게시글 조회수 증가 ====================

    @Transactional
    public void incrementViewCount(String postId) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));
        postRepository.incrementViewCount(postId);
        log.info("게시글 조회수 증가. postId: {}", postId);
    }

    // ==================== 게시글 상세 조회 ====================

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(String postId, String userId) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        // 이미지 조회
        List<PostImageResponse> images = postImageRepository.findByPostIdOrderBySortOrderAsc(postId)
                .stream()
                .map(PostImageResponse::from)
                .collect(Collectors.toList());

        // 댓글 조회 (대댓글 포함)
        List<CommentResponse> comments = getCommentsWithReplies(postId);

        // 좋아요 여부
        boolean isLiked = userId != null && likeRepository.existsByPostIdAndUserId(postId, userId);

        // 작성자 여부
        boolean isOwner = userId != null && userId.equals(post.getAuthorId());

        return PostDetailResponse.from(post, images, comments, isLiked, isOwner);
    }

    public List<CommentResponse> getComments(String postId) {
        return getCommentsWithReplies(postId);
    }

    private List<CommentResponse> getCommentsWithReplies(String postId) {
        List<CommunityComment> rootComments = commentRepository.findRootCommentsByPostId(postId);

        return rootComments.stream()
                .map(comment -> {
                    CommentResponse response = CommentResponse.from(comment);
                    List<CommunityComment> replies = commentRepository.findRepliesByParentId(comment.getCommentId());
                    response.setReplies(replies.stream().map(CommentResponse::from).collect(Collectors.toList()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    // ==================== 게시글 등록 ====================

    @Transactional
    public PostResponse createPost(String authorId, String authorName, String authorImage, String centerId, PostRequest request) {
        CommunityPost.PostCategory category = CommunityPost.PostCategory.valueOf(request.getCategory());

        CommunityPost.TradeStatus tradeStatus = null;
        if (category == CommunityPost.PostCategory.club_sale || category == CommunityPost.PostCategory.used) {
            tradeStatus = CommunityPost.TradeStatus.available;
        }

        CommunityPost post = CommunityPost.builder()
                .centerId(centerId)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .authorId(authorId)
                .authorName(authorName)
                .authorImage(authorImage)
                .price(request.getPrice())
                .location(request.getLocation())
                .locationDetail(request.getLocationDetail())
                .contact(request.getContact())
                .tradeStatus(tradeStatus)
                .viewCount(0)
                .commentCount(0)
                .likeCount(0)
                .isNotice(false)
                .isDeleted(false)
                .build();

        CommunityPost saved = postRepository.save(post);
        log.info("게시글 등록. postId: {}, authorId: {}", saved.getPostId(), authorId);

        // 이미지 저장
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            savePostImages(saved.getPostId(), request.getImageUrls());
        }

        return PostResponse.from(saved);
    }

    private void savePostImages(String postId, List<String> imageUrls) {
        int order = 0;
        for (String imageUrl : imageUrls) {
            CommunityPostImage image = CommunityPostImage.builder()
                    .postId(postId)
                    .imageUrl(imageUrl)
                    .sortOrder(order++)
                    .build();
            postImageRepository.save(image);
        }
    }

    // ==================== 게시글 수정 ====================

    @Transactional
    public PostResponse updatePost(String postId, String userId, PostRequest request) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPrice(request.getPrice());
        post.setLocation(request.getLocation());
        post.setLocationDetail(request.getLocationDetail());
        post.setContact(request.getContact());

        CommunityPost saved = postRepository.save(post);
        log.info("게시글 수정. postId: {}", postId);

        // 이미지 업데이트
        if (request.getImageUrls() != null) {
            postImageRepository.deleteByPostId(postId);
            savePostImages(postId, request.getImageUrls());
        }

        return PostResponse.from(saved);
    }

    // ==================== 게시글 삭제 ====================

    @Transactional
    public void deletePost(String postId, String userId) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }

        post.setIsDeleted(true);
        postRepository.save(post);
        log.info("게시글 삭제 (soft delete). postId: {}", postId);
    }

    // ==================== 거래 상태 변경 ====================

    @Transactional
    public TradeStatusResponse changeTradeStatus(String postId, String userId, String status) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("거래 상태 변경 권한이 없습니다.");
        }

        post.setTradeStatus(CommunityPost.TradeStatus.valueOf(status));
        CommunityPost saved = postRepository.save(post);
        log.info("거래 상태 변경. postId: {}, status: {}", postId, status);

        return TradeStatusResponse.builder()
                .postId(saved.getPostId())
                .tradeStatus(saved.getTradeStatus().name())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // ==================== 좋아요 토글 ====================

    @Transactional
    public LikeResponse toggleLike(String postId, String userId) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boolean liked;
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            postRepository.decrementLikeCount(postId);
            post.decrementLikeCount();
            liked = false;
        } else {
            CommunityLike like = CommunityLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();
            likeRepository.save(like);
            postRepository.incrementLikeCount(postId);
            post.incrementLikeCount();
            liked = true;
        }

        log.info("좋아요 토글. postId: {}, userId: {}, liked: {}", postId, userId, liked);

        return LikeResponse.builder()
                .liked(liked)
                .likeCount(post.getLikeCount())
                .build();
    }

    // ==================== 댓글 등록 ====================

    @Transactional
    public CommentResponse createComment(String userId, String userName, String userImage, CommentRequest request) {
        CommunityPost post = postRepository.findByPostIdAndIsDeletedFalse(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 대댓글인 경우 부모 댓글 확인
        if (request.getParentId() != null) {
            commentRepository.findByCommentIdAndIsDeletedFalse(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        CommunityComment comment = CommunityComment.builder()
                .postId(request.getPostId())
                .parentId(request.getParentId())
                .authorId(userId)
                .authorName(userName)
                .authorImage(userImage)
                .content(request.getContent())
                .isDeleted(false)
                .build();

        CommunityComment saved = commentRepository.save(comment);

        // 댓글 수 증가
        postRepository.incrementCommentCount(request.getPostId());
        log.info("댓글 등록. commentId: {}, postId: {}", saved.getCommentId(), request.getPostId());

        return CommentResponse.from(saved);
    }

    // ==================== 댓글 삭제 ====================

    @Transactional
    public void deleteComment(String commentId, String userId) {
        CommunityComment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        // 댓글 수 감소
        postRepository.decrementCommentCount(comment.getPostId());
        log.info("댓글 삭제 (soft delete). commentId: {}", commentId);
    }

    // ==================== 협력사 목록 조회 ====================

    public PartnerListResponse getPartners(String category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommunityPartner> partnersPage = partnerRepository.findByFilters(category, keyword, pageable);
        long totalCount = partnerRepository.countByFilters(category, keyword);

        List<PartnerResponse> partnerResponses = partnersPage.getContent().stream()
                .map(PartnerResponse::from)
                .collect(Collectors.toList());

        return PartnerListResponse.builder()
                .partners(partnerResponses)
                .totalCount(totalCount)
                .build();
    }
}
