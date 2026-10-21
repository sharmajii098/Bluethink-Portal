package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.TalentSuccessionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TalentSuccessionPlanRepository extends JpaRepository<TalentSuccessionPlan,Long>{
    List<TalentSuccessionPlan> findAllByOrderByCreatedAtDesc();
    List<TalentSuccessionPlan> findByEmployeeUserIdOrderByCreatedAtDesc(String employeeUserId);
    boolean existsByEmployeeUserIdAndTargetRoleIgnoreCaseAndStatusIn(String employeeUserId,String targetRole,Collection<String> statuses);
}
