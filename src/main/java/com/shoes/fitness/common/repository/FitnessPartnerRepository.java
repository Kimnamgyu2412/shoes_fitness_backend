package com.shoes.fitness.common.repository;

import com.shoes.fitness.entity.FitnessPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitnessPartnerRepository extends JpaRepository<FitnessPartner, String> {

    Optional<FitnessPartner> findByFitnessLoginId(String fitnessLoginId);

    Optional<FitnessPartner> findByOwnerEmail(String ownerEmail);

    Optional<FitnessPartner> findByBusinessNumber(String businessNumber);

    boolean existsByFitnessLoginId(String fitnessLoginId);

    boolean existsByOwnerEmail(String ownerEmail);

    boolean existsByBusinessNumber(String businessNumber);
}
