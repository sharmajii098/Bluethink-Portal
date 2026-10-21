package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeSurveyResponse; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeSurveyResponseRepository extends JpaRepository<EmployeeSurveyResponse,Long>{ Optional<EmployeeSurveyResponse> findBySurvey_IdAndUserId(Long id,String u); List<EmployeeSurveyResponse> findBySurvey_Id(Long id); long countBySurvey_Id(Long id); }
