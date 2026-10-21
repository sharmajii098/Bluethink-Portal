package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CandidateRepository extends JpaRepository<Candidate,Long>{ List<Candidate> findAllByOrderByUpdatedAtDesc(); List<Candidate> findByJobOpening_IdOrderByUpdatedAtDesc(Long id); List<Candidate> findByStageOrderByUpdatedAtDesc(String stage);
 List<Candidate> findByJobOpening_DepartmentIgnoreCaseOrderByUpdatedAtDesc(String department); }
