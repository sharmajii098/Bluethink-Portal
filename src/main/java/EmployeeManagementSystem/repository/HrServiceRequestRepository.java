package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.HrServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface HrServiceRequestRepository extends JpaRepository<HrServiceRequest,Long>{
 List<HrServiceRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<HrServiceRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndSubjectIgnoreCaseAndStatusIn(String userId,String subject,Collection<String> statuses);
}
