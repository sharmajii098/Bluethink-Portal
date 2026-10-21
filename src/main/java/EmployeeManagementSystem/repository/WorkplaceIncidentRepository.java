package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.WorkplaceIncident; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WorkplaceIncidentRepository extends JpaRepository<WorkplaceIncident,Long>{
 List<WorkplaceIncident> findByReporterUserIdOrderByCreatedAtDesc(String userId);
 List<WorkplaceIncident> findAllByOrderByCreatedAtDesc();
}
