package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.BenefitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BenefitPlanRepository extends JpaRepository<BenefitPlan,Long>{ List<BenefitPlan> findByActiveTrueOrderByCategoryAscPlanNameAsc(); boolean existsByPlanCode(String planCode); }
