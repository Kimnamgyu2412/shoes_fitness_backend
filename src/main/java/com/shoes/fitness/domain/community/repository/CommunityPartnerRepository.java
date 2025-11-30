package com.shoes.fitness.domain.community.repository;

import com.shoes.fitness.entity.CommunityPartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPartnerRepository extends JpaRepository<CommunityPartner, String> {

    @Query("SELECT p FROM CommunityPartner p WHERE p.isActive = true " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:keyword IS NULL OR p.name LIKE %:keyword%) " +
            "ORDER BY p.sortOrder ASC, p.createdAt DESC")
    Page<CommunityPartner> findByFilters(
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM CommunityPartner p WHERE p.isActive = true " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:keyword IS NULL OR p.name LIKE %:keyword%)")
    long countByFilters(
            @Param("category") String category,
            @Param("keyword") String keyword);
}
