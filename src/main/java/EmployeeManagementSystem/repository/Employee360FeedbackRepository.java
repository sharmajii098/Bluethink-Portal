package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.Employee360Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface Employee360FeedbackRepository extends JpaRepository<Employee360Feedback,Long>{
 List<Employee360Feedback> findByReviewerUserIdOrderByCreatedAtDesc(String userId);
 List<Employee360Feedback> findBySubjectUserIdOrderByCreatedAtDesc(String userId);
 List<Employee360Feedback> findAllByOrderByCreatedAtDesc();
 boolean existsByReviewerUserIdAndSubjectUserIdAndStatusIn(String reviewerUserId,String subjectUserId,Collection<String> statuses);
}
