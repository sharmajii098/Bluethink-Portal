package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// LeaveBalanceRepository.java
@Repository
// LeaveBalanceRepository.java
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    // ✅ CORRECT: Use l.employee.id (not l.employeeId)
    @Query("SELECT l FROM LeaveBalance l WHERE l.employee.id = :employeeId AND l.leaveType.id = :leaveTypeId AND l.year = :year")
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(@Param("employeeId") Long employeeId,
                                                                 @Param("leaveTypeId") Long leaveTypeId,
                                                                 @Param("year") Integer year);
    List<LeaveBalance> findByEmployee_Id(Long employeeId);
}