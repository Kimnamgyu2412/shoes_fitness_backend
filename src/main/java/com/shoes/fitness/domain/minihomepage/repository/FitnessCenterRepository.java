package com.shoes.fitness.domain.minihomepage.repository;

import com.shoes.fitness.entity.FitnessCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitnessCenterRepository extends JpaRepository<FitnessCenter, String> {

    Optional<FitnessCenter> findByFitnessId(String fitnessId);

    boolean existsByFitnessId(String fitnessId);
}
