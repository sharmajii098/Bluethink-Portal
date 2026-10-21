package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.TimesheetLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimesheetLockRepository extends JpaRepository<TimesheetLock, Long> {
    Optional<TimesheetLock> findByEmployeeIdAndLockDate(String employeeId, LocalDate lockDate);
    List<TimesheetLock> findByEmployeeIdOrderByLockDateDesc(String employeeId);
    List<TimesheetLock> findByStatusOrderByLockDateDesc(String status);
}
