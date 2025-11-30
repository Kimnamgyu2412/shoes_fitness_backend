package com.shoes.fitness.domain.jobposting.repository;

import com.shoes.fitness.entity.FitnessJobPostingRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRequirementRepository extends JpaRepository<FitnessJobPostingRequirement, String> {

    List<FitnessJobPostingRequirement> findByPostingId(String postingId);

    void deleteByPostingId(String postingId);
}
