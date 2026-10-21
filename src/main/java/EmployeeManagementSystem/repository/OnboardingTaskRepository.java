package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.OnboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask,Long>{ List<OnboardingTask> findByEmployeeIdOrderByDueDateAscIdAsc(Long employeeId); long countByEmployeeIdAndStatus(Long employeeId,String status); List<OnboardingTask> findByEmployeeIdInOrderByDueDateAscIdAsc(List<Long> employeeIds); List<OnboardingTask> findAllByOrderByDueDateAscIdAsc(); }
