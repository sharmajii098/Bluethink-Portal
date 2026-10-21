package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.GrievanceComment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface GrievanceCommentRepository extends JpaRepository<GrievanceComment,Long>{ List<GrievanceComment> findByGrievanceCase_IdOrderByCreatedAtAsc(Long id); }
