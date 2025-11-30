package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessCenterFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessCenterFacilityRepository extends JpaRepository<FitnessCenterFacility, String> {

    List<FitnessCenterFacility> findByCenterId(String centerId);

    List<FitnessCenterFacility> findByCenterIdAndIsActiveTrue(String centerId);

    Optional<FitnessCenterFacility> findByCenterIdAndFacilityCode(String centerId, String facilityCode);

    @Modifying
    @Query("DELETE FROM FitnessCenterFacility f WHERE f.centerId = :centerId")
    void deleteByCenterId(@Param("centerId") String centerId);

    @Modifying
    @Query("UPDATE FitnessCenterFacility f SET f.isActive = :isActive WHERE f.centerId = :centerId AND f.facilityCode = :facilityCode")
    void updateIsActive(@Param("centerId") String centerId, @Param("facilityCode") String facilityCode, @Param("isActive") Boolean isActive);
}
