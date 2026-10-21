package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.LeaveEncashmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface LeaveEncashmentRequestRepository extends JpaRepository<LeaveEncashmentRequest, Long> {
    List<LeaveEncashmentRequest> findByUserIdOrderByCreatedAtDesc(String userId);
    List<LeaveEncashmentRequest> findAllByOrderByCreatedAtDesc();
    boolean existsByUserIdAndFinancialYearAndLeaveTypeAndStatusIn(String userId, String financialYear, String leaveType, Collection<String> statuses);
}
