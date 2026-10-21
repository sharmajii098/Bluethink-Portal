package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeRecognition; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployeeRecognitionRepository extends JpaRepository<EmployeeRecognition,Long>{List<EmployeeRecognition> findByNomineeUserIdOrNominatedByUserIdOrderByCreatedAtDesc(String nominee,String nominator); List<EmployeeRecognition> findAllByOrderByCreatedAtDesc(); boolean existsByRecognitionNumber(String number);}
