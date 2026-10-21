package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.PayrollChangeRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PayrollChangeRequestRepository extends JpaRepository<PayrollChangeRequest,Long>{
 List<PayrollChangeRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<PayrollChangeRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndChangeTypeAndStatusIn(String userId,String changeType,List<String> statuses);
}
