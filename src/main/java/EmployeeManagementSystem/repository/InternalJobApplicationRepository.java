package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.InternalJobApplication; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface InternalJobApplicationRepository extends JpaRepository<InternalJobApplication,Long>{List<InternalJobApplication> findByApplicantUserIdOrderByCreatedAtDesc(String u); List<InternalJobApplication> findAllByOrderByCreatedAtDesc(); boolean existsByPostingIdAndApplicantUserId(Long p,String u);}
