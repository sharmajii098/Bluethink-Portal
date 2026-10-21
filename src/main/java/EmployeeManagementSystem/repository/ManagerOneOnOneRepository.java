package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.ManagerOneOnOne;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface ManagerOneOnOneRepository extends JpaRepository<ManagerOneOnOne,Long>{
 List<ManagerOneOnOne> findByManagerUserIdOrEmployeeUserIdOrderByMeetingDateDesc(String managerUserId,String employeeUserId);
 List<ManagerOneOnOne> findAllByOrderByMeetingDateDesc();
 boolean existsByManagerUserIdAndEmployeeUserIdAndMeetingDateAndStatusIn(String manager,String employee,LocalDate date,java.util.Set<String> statuses);
}
