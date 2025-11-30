package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessCenterOperationHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitnessCenterOperationHoursRepository extends JpaRepository<FitnessCenterOperationHours, String> {

    Optional<FitnessCenterOperationHours> findByCenterId(String centerId);

    @Modifying
    @Query("DELETE FROM FitnessCenterOperationHours h WHERE h.centerId = :centerId")
    void deleteByCenterId(@Param("centerId") String centerId);
}
