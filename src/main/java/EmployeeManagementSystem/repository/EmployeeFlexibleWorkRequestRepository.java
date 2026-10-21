package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeFlexibleWorkRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeFlexibleWorkRequestRepository extends JpaRepository<EmployeeFlexibleWorkRequest,Long> {
 List<EmployeeFlexibleWorkRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeFlexibleWorkRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndStatusIn(String userId, List<String> statuses);
}
