package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeAccessCardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmployeeAccessCardRequestRepository extends JpaRepository<EmployeeAccessCardRequest,Long>{
 List<EmployeeAccessCardRequest> findByUserIdOrderByCreatedAtDesc(String userId);
 List<EmployeeAccessCardRequest> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndRequestTypeAndStatusIn(String userId,String requestType,Collection<String> statuses);
}
