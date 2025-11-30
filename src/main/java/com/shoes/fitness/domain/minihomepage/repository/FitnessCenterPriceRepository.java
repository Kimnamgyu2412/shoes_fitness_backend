package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessCenterPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessCenterPriceRepository extends JpaRepository<FitnessCenterPrice, String> {

    List<FitnessCenterPrice> findByCenterIdOrderBySortOrderAsc(String centerId);

    List<FitnessCenterPrice> findByCenterIdAndIsActiveTrueOrderBySortOrderAsc(String centerId);

    @Modifying
    @Query("DELETE FROM FitnessCenterPrice p WHERE p.centerId = :centerId")
    void deleteByCenterId(@Param("centerId") String centerId);
}
