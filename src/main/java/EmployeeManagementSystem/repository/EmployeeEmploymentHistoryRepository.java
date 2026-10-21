package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeEmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeEmploymentHistoryRepository extends JpaRepository<EmployeeEmploymentHistory, Long> {
    List<EmployeeEmploymentHistory> findByEmployeeIdOrderByEndDateDescIdDesc(String employeeId);
    Optional<EmployeeEmploymentHistory> findByIdAndEmployeeId(Long id, String employeeId);
}
