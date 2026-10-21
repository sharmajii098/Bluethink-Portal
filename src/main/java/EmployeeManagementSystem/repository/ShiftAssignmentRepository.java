package EmployeeManagementSystem.repository; import EmployeeManagementSystem.entity.ShiftAssignment; import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType; import java.time.LocalDate; import java.util.*; public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment,Long>{ List<ShiftAssignment> findByEmployee_IdOrderByShiftDateDesc(Long id); List<ShiftAssignment> findByShiftDateBetweenOrderByShiftDateAsc(LocalDate from,LocalDate to); Optional<ShiftAssignment> findByEmployee_IdAndShiftDate(Long employeeId,LocalDate date); Optional<ShiftAssignment> findByEmployee_RegisterEmployee_UserIdAndShiftDate(String userId, LocalDate date); Optional<ShiftAssignment> findByEmployee_EmployeeProfile_UserIdAndShiftDate(String userId, LocalDate date);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select a from ShiftAssignment a where a.id = :id")
 Optional<ShiftAssignment> lockAssignment(@Param("id") Long id); }
