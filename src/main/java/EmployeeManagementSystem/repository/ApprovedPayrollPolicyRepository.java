package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.ApprovedPayrollPolicy;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface ApprovedPayrollPolicyRepository extends JpaRepository<ApprovedPayrollPolicy,Long> {
 Optional<ApprovedPayrollPolicy> findByReference(String reference);
 List<ApprovedPayrollPolicy> findAllByOrderByCreatedAtDesc();
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select p from ApprovedPayrollPolicy p where p.id=:id")
 Optional<ApprovedPayrollPolicy> lock(@Param("id") Long id);
}
