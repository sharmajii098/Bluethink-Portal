package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.JobOpening;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JobOpeningRepository extends JpaRepository<JobOpening,Long>{ List<JobOpening> findByStatusOrderByCreatedAtDesc(String status);
 List<JobOpening> findByStatusAndDepartmentIgnoreCaseOrderByCreatedAtDesc(String status, String department); }
