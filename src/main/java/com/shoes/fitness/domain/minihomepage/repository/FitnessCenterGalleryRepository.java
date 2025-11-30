package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessCenterGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessCenterGalleryRepository extends JpaRepository<FitnessCenterGallery, String> {

    List<FitnessCenterGallery> findByCenterIdOrderBySortOrderAsc(String centerId);

    @Modifying
    @Query("DELETE FROM FitnessCenterGallery g WHERE g.centerId = :centerId")
    void deleteByCenterId(@Param("centerId") String centerId);

    @Modifying
    @Query("UPDATE FitnessCenterGallery g SET g.sortOrder = :sortOrder WHERE g.galleryId = :galleryId")
    void updateSortOrder(@Param("galleryId") String galleryId, @Param("sortOrder") Integer sortOrder);
}
