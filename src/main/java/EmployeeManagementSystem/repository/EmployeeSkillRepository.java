package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmployeeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill,Long>{
 List<EmployeeSkill> findByUserIdOrderBySkillNameAsc(String userId);
 List<EmployeeSkill> findAllByOrderByCreatedAtDesc();
 boolean existsByUserIdAndSkillNameIgnoreCaseAndStatusIn(String userId,String skillName,Collection<String> statuses);
}
