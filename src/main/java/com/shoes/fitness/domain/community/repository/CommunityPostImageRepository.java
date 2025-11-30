package com.shoes.fitness.domain.community.repository;

import com.shoes.fitness.entity.CommunityPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostImageRepository extends JpaRepository<CommunityPostImage, String> {

    List<CommunityPostImage> findByPostIdOrderBySortOrderAsc(String postId);

    void deleteByPostId(String postId);
}
