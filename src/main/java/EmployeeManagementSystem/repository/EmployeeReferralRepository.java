package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeReferral; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeReferralRepository extends JpaRepository<EmployeeReferral,Long>{List<EmployeeReferral> findByReferrerUserIdOrderByCreatedAtDesc(String userId); List<EmployeeReferral> findAllByOrderByCreatedAtDesc(); boolean existsByReferrerUserIdAndCandidateEmailAndStatusIn(String u,String e,List<String>s);}
