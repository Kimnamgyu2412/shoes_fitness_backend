package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessFacilityMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessFacilityMasterRepository extends JpaRepository<FitnessFacilityMaster, String> {

    List<FitnessFacilityMaster> findByIsActiveTrueOrderBySortOrderAsc();
}
