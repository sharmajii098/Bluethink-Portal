package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeTransferRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeTransferRequestRepository extends JpaRepository<EmployeeTransferRequest,Long>{
 List<EmployeeTransferRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeTransferRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndStatusIn(String userId,List<String> statuses);
}
