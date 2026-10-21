package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeeBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface EmployeeBeneficiaryRepository extends JpaRepository<EmployeeBeneficiary,Long>{
 List<EmployeeBeneficiary> findByUserIdOrderByBenefitTypeAscAllocationPercentageDescCreatedAtAsc(String userId);
 List<EmployeeBeneficiary> findAllByOrderByCreatedAtDesc();
 List<EmployeeBeneficiary> findByUserIdAndBenefitTypeOrderByCreatedAtAsc(String userId,String benefitType);
 boolean existsByUserIdAndBenefitTypeAndNomineeNameIgnoreCase(String userId,String benefitType,String nomineeName);
}
