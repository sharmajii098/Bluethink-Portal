package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeePolicyAcknowledgement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmployeePolicyAcknowledgementRepository extends JpaRepository<EmployeePolicyAcknowledgement,Long>{
 List<EmployeePolicyAcknowledgement> findByUserIdOrderByDueDateAscCreatedAtDesc(String userId);
 List<EmployeePolicyAcknowledgement> findAllByOrderByDueDateAscCreatedAtDesc();
 boolean existsByUserIdAndTitleAndVersion(String userId,String title,String version);
}
