package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface EmployeeAvailabilityRepository extends JpaRepository<EmployeeAvailability,Long>{
 List<EmployeeAvailability> findByUserIdOrderByStartDateDesc(String userId);
 List<EmployeeAvailability> findAllByOrderByStartDateDesc();
 boolean existsByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIn(String userId, LocalDate endDate, LocalDate startDate, Collection<String> statuses);
}
