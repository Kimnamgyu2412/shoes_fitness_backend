package com.shoes.fitness.domain.jobposting.repository;

import com.shoes.fitness.entity.FitnessRequirementMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementMasterRepository extends JpaRepository<FitnessRequirementMaster, String> {

    List<FitnessRequirementMaster> findByIsActiveTrueOrderBySortOrderAsc();

    List<FitnessRequirementMaster> findByRequirementCodeIn(List<String> codes);
}
