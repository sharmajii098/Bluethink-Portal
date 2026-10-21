package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByUserIdOrderByUpdatedAtDesc(String userId);
    List<PerformanceReview> findByEmployee_IdOrderByUpdatedAtDesc(Long employeeId);
    List<PerformanceReview> findByEmployee_ReportingManager_IdOrderByUpdatedAtDesc(Long managerId);
    List<PerformanceReview> findAllByOrderByUpdatedAtDesc();
}
