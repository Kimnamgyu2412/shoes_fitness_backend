package com.shoes.fitness.domain.community.repository;

import com.shoes.fitness.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, String> {

    @Query("SELECT c FROM CommunityComment c WHERE c.postId = :postId AND c.parentId IS NULL AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<CommunityComment> findRootCommentsByPostId(@Param("postId") String postId);

    @Query("SELECT c FROM CommunityComment c WHERE c.parentId = :parentId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<CommunityComment> findRepliesByParentId(@Param("parentId") String parentId);

    Optional<CommunityComment> findByCommentIdAndIsDeletedFalse(String commentId);

    @Query("SELECT COUNT(c) FROM CommunityComment c WHERE c.postId = :postId AND c.isDeleted = false")
    long countByPostId(@Param("postId") String postId);
}
