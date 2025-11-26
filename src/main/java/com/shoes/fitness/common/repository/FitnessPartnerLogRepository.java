package com.shoes.fitness.common.repository;

import com.shoes.fitness.entity.FitnessPartnerLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FitnessPartnerLogRepository extends JpaRepository<FitnessPartnerLog, Long> {

    List<FitnessPartnerLog> findByFitnessIdOrderByCreatedAtDesc(String fitnessId);

    Page<FitnessPartnerLog> findByFitnessId(String fitnessId, Pageable pageable);

    List<FitnessPartnerLog> findByFitnessIdAndActionType(String fitnessId, FitnessPartnerLog.ActionType actionType);

    List<FitnessPartnerLog> findByFitnessIdAndCreatedAtBetween(
            String fitnessId, LocalDateTime startDate, LocalDateTime endDate);

    long countByFitnessIdAndActionType(String fitnessId, FitnessPartnerLog.ActionType actionType);

    @Query("SELECT COUNT(l) FROM FitnessPartnerLog l WHERE l.fitnessId = :fitnessId " +
           "AND l.actionType = 'LOGIN_FAIL' AND l.createdAt > :since")
    long countRecentLoginFailures(@Param("fitnessId") String fitnessId, @Param("since") LocalDateTime since);
}
