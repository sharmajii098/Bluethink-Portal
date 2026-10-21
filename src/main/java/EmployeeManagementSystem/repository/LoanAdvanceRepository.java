package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.LoanAdvance; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface LoanAdvanceRepository extends JpaRepository<LoanAdvance,Long>{ List<LoanAdvance> findByUserIdOrderByCreatedAtDesc(String userId); List<LoanAdvance> findAllByOrderByCreatedAtDesc(); Optional<LoanAdvance> findByRequestNumber(String n); }
