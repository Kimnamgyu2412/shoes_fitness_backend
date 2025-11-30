package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessCenterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessCenterEventRepository extends JpaRepository<FitnessCenterEvent, String> {

    List<FitnessCenterEvent> findByCenterIdOrderByStartDateDesc(String centerId);

    List<FitnessCenterEvent> findByCenterIdAndIsActiveTrueOrderByStartDateDesc(String centerId);

    @Modifying
    @Query("DELETE FROM FitnessCenterEvent e WHERE e.centerId = :centerId")
    void deleteByCenterId(@Param("centerId") String centerId);
}
