package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.BenefitEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BenefitEnrollmentRepository extends JpaRepository<BenefitEnrollment,Long>{ List<BenefitEnrollment> findByUserIdOrderByCreatedAtDesc(String userId); List<BenefitEnrollment> findAllByOrderByCreatedAtDesc(); Optional<BenefitEnrollment> findByUserIdAndBenefitPlan_Id(String userId,Long planId); }
