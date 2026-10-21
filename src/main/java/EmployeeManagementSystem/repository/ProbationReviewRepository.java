package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.ProbationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProbationReviewRepository extends JpaRepository<ProbationReview,Long>{
 List<ProbationReview> findAllByOrderByProbationEndDateAsc();
 List<ProbationReview> findByEmployeeUserIdOrderByCreatedAtDesc(String employeeUserId);
 Optional<ProbationReview> findTopByEmployeeUserIdOrderByCreatedAtDesc(String employeeUserId);
}
