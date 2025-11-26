package com.shoes.fitness.common.repository;

import com.shoes.fitness.entity.RefreshFitnessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshFitnessTokenRepository extends JpaRepository<RefreshFitnessToken, Long> {

    Optional<RefreshFitnessToken> findByToken(String token);

    Optional<RefreshFitnessToken> findByFitnessId(String fitnessId);

    void deleteByFitnessId(String fitnessId);

    void deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshFitnessToken r WHERE r.expiryDate < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RefreshFitnessToken r WHERE r.absoluteExpiryDate < :now")
    int deleteAbsoluteExpiredTokens(@Param("now") LocalDateTime now);

    boolean existsByFitnessId(String fitnessId);
}
