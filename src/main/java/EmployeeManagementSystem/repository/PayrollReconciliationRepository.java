package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.PayrollReconciliation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface PayrollReconciliationRepository extends JpaRepository<PayrollReconciliation,Long>{
 List<PayrollReconciliation> findByPeriodOrderByProfileId(String period);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select r from PayrollReconciliation r where r.id=:id") Optional<PayrollReconciliation> lock(@Param("id") Long id);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select r from PayrollReconciliation r where r.profileId=:profile and r.period=:period") Optional<PayrollReconciliation> lockPeriod(@Param("profile") Long profile,@Param("period") String period);
}
