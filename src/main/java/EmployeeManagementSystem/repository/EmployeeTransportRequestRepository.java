package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeTransportRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeTransportRequestRepository extends JpaRepository<EmployeeTransportRequest,Long>{
 List<EmployeeTransportRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeTransportRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndRequestTypeAndStatusIn(String userId,String requestType,List<String> statuses);
}
