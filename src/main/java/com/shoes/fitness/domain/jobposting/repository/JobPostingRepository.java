package com.shoes.fitness.domain.jobposting.repository;

import com.shoes.fitness.entity.FitnessJobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<FitnessJobPosting, String> {

    List<FitnessJobPosting> findByCenterIdAndIsActiveTrueOrderByCreatedAtDesc(String centerId);

    Page<FitnessJobPosting> findByCenterIdAndIsActiveTrue(String centerId, Pageable pageable);

    @Query("SELECT p FROM FitnessJobPosting p WHERE p.centerId = :centerId AND p.isActive = true " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword%) " +
            "ORDER BY p.createdAt DESC")
    Page<FitnessJobPosting> findByFilters(
            @Param("centerId") String centerId,
            @Param("status") FitnessJobPosting.PostingStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM FitnessJobPosting p WHERE p.centerId = :centerId AND p.isActive = true " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword%)")
    long countByFilters(
            @Param("centerId") String centerId,
            @Param("status") FitnessJobPosting.PostingStatus status,
            @Param("keyword") String keyword);

    Optional<FitnessJobPosting> findByPostingIdAndCenterId(String postingId, String centerId);

    @Modifying
    @Query("UPDATE FitnessJobPosting p SET p.viewCount = p.viewCount + 1 WHERE p.postingId = :postingId")
    void incrementViewCount(@Param("postingId") String postingId);
}
