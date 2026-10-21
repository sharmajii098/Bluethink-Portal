package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmployeeComplianceRecordRepository extends JpaRepository<EmployeeComplianceRecord,Long>{
 List<EmployeeComplianceRecord> findByUserIdOrderByExpiryDateAsc(String userId);
 List<EmployeeComplianceRecord> findAllByOrderByExpiryDateAsc();
 boolean existsByUserIdAndDocumentTypeAndDocumentNameIgnoreCaseAndStatusIn(String userId,String documentType,String documentName,List<String> statuses);
}
