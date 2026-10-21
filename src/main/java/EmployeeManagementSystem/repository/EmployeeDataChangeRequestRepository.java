package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeDataChangeRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeDataChangeRequestRepository extends JpaRepository<EmployeeDataChangeRequest,Long>{
 List<EmployeeDataChangeRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeDataChangeRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndFieldNameAndStatusIn(String userId,String fieldName,List<String> statuses);
}
