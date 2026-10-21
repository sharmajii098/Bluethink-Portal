package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.*;

public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {
    List<ShiftSwapRequest> findByRequesterUserIdOrTargetUserIdOrderByCreatedAtDesc(String requesterUserId, String targetUserId);
    List<ShiftSwapRequest> findAllByOrderByCreatedAtDesc();
    boolean existsByRequesterUserIdAndRequesterDateAndStatusIn(String userId, LocalDate date, Collection<String> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from ShiftSwapRequest r where r.id = :id")
    Optional<ShiftSwapRequest> lockById(@Param("id") Long id);

    @Query("select count(r) from ShiftSwapRequest r where r.status in :statuses and ((r.requesterUserId = :user and r.requesterDate = :date) or (r.targetUserId = :user and r.targetDate = :date))")
    long countActiveForShift(@Param("user") String user, @Param("date") LocalDate date,
                             @Param("statuses") Collection<String> statuses);
}
