package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeEducationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeEducationRecordRepository extends JpaRepository<EmployeeEducationRecord, Long> {
    List<EmployeeEducationRecord> findByEmployeeIdOrderByEndDateDescIdDesc(String employeeId);
    Optional<EmployeeEducationRecord> findByIdAndEmployeeId(Long id, String employeeId);
}
