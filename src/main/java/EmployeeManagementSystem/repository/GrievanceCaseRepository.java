package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.GrievanceCase; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface GrievanceCaseRepository extends JpaRepository<GrievanceCase,Long>{ List<GrievanceCase> findByUserIdOrderByUpdatedAtDesc(String userId); List<GrievanceCase> findAllByOrderByUpdatedAtDesc(); Optional<GrievanceCase> findTopByOrderByIdDesc(); }
