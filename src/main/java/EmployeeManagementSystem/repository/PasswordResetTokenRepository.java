package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    List<PasswordResetToken> findByAccountIdAndConsumedAtIsNull(Long accountId);
    Optional<PasswordResetToken> findTopByAccountIdOrderByCreatedAtDesc(Long accountId);
    long countByAccountIdAndCreatedAtAfter(Long accountId, Instant after);

    /** Atomically claim a reset link so it cannot be used in two simultaneous requests. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update PasswordResetToken t set t.consumedAt = :now " +
           "where t.id = :id and t.consumedAt is null and t.expiresAt > :now")
    int consumeIfValid(@Param("id") Long id, @Param("now") Instant now);
}
