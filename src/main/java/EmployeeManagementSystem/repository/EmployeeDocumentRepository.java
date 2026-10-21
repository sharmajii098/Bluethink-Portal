package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.*;
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument,Long>{
 List<EmployeeDocument> findByEmployeeIdOrderByUploadedAtDesc(String employeeId);
 List<EmployeeDocument> findAllByOrderByUploadedAtDesc();
 long countByStatus(String status);
 long countByExpiryDateBetween(LocalDate from, LocalDate to);
 Optional<EmployeeDocument> findByIdAndEmployeeId(Long id,String employeeId);
}
