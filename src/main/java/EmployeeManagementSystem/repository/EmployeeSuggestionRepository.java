package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmployeeSuggestionRepository extends JpaRepository<EmployeeSuggestion,Long>{
    List<EmployeeSuggestion> findByUserIdOrderByCreatedAtDesc(String userId);
    List<EmployeeSuggestion> findAllByOrderByCreatedAtDesc();
}
