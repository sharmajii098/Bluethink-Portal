package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.OvertimeRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.time.LocalDate; import java.util.*;
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest,Long>{
 List<OvertimeRequest> findByUserIdOrderByWorkDateDescCreatedAtDesc(String userId);
 List<OvertimeRequest> findAllByOrderByWorkDateDescCreatedAtDesc();
 boolean existsByUserIdAndWorkDateAndStatusIn(String userId,LocalDate date,Collection<String> statuses);
}
