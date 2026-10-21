package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeSurvey; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeSurveyRepository extends JpaRepository<EmployeeSurvey,Long>{ Optional<EmployeeSurvey> findBySurveyNumber(String n); List<EmployeeSurvey> findAllByOrderByCreatedAtDesc(); }
