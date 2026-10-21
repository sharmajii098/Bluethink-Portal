package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeShiftPreferenceRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeShiftPreferenceRequestRepository extends JpaRepository<EmployeeShiftPreferenceRequest,Long>{
 List<EmployeeShiftPreferenceRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeShiftPreferenceRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndStatusIn(String userId, Collection<String> statuses);
}
