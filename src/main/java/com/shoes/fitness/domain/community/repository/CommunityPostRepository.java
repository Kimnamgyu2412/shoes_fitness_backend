package com.shoes.fitness.domain.community.repository;

import com.shoes.fitness.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, String> {

    @Query("SELECT p FROM CommunityPost p WHERE p.isDeleted = false " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:status IS NULL OR p.tradeStatus = :status) " +
            "AND (:location IS NULL OR p.location LIKE %:location%) " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
            "ORDER BY p.isNotice DESC, p.createdAt DESC")
    Page<CommunityPost> findByFilters(
            @Param("category") CommunityPost.PostCategory category,
            @Param("status") CommunityPost.TradeStatus status,
            @Param("location") String location,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM CommunityPost p WHERE p.isDeleted = false " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:status IS NULL OR p.tradeStatus = :status) " +
            "AND (:location IS NULL OR p.location LIKE %:location%) " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    long countByFilters(
            @Param("category") CommunityPost.PostCategory category,
            @Param("status") CommunityPost.TradeStatus status,
            @Param("location") String location,
            @Param("keyword") String keyword);

    Optional<CommunityPost> findByPostIdAndIsDeletedFalse(String postId);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") String postId);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.commentCount = p.commentCount + 1 WHERE p.postId = :postId")
    void incrementCommentCount(@Param("postId") String postId);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.commentCount = CASE WHEN p.commentCount > 0 THEN p.commentCount - 1 ELSE 0 END WHERE p.postId = :postId")
    void decrementCommentCount(@Param("postId") String postId);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.likeCount = p.likeCount + 1 WHERE p.postId = :postId")
    void incrementLikeCount(@Param("postId") String postId);

    @Modifying
    @Query("UPDATE CommunityPost p SET p.likeCount = CASE WHEN p.likeCount > 0 THEN p.likeCount - 1 ELSE 0 END WHERE p.postId = :postId")
    void decrementLikeCount(@Param("postId") String postId);
}
