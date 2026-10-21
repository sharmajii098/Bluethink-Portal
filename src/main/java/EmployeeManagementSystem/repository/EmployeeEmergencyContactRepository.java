package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmployeeEmergencyContactRepository extends JpaRepository<EmployeeEmergencyContact,Long>{
 List<EmployeeEmergencyContact> findByUserIdOrderByPrimaryContactDescCreatedAtAsc(String userId);
 List<EmployeeEmergencyContact> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndContactTypeAndContactNameIgnoreCase(String userId,String contactType,String contactName);
}
