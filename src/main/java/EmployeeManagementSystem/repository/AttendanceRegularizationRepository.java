package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.AttendanceRegularization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
public interface AttendanceRegularizationRepository extends JpaRepository<AttendanceRegularization,Long>{
 List<AttendanceRegularization> findByEmployeeUserIdOrderByCreatedAtDesc(String employeeUserId);
 List<AttendanceRegularization> findAllByOrderByCreatedAtDesc();
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select r from AttendanceRegularization r where r.id = :id")
 Optional<AttendanceRegularization> findLockedById(@Param("id") Long id);
 boolean existsByEmployeeUserIdAndAttendanceDateAndStatusIn(String userId, LocalDate date, List<String> statuses);
}
