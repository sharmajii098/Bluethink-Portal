package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.InternalJobPosting; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface InternalJobPostingRepository extends JpaRepository<InternalJobPosting,Long>{List<InternalJobPosting> findAllByOrderByCreatedAtDesc(); List<InternalJobPosting> findByStatusOrderByApplicationDeadlineAsc(String status);}
