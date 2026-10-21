package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.EmploymentContract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface EmploymentContractRepository extends JpaRepository<EmploymentContract,Long>{
 List<EmploymentContract> findByUserIdOrderByStartDateDesc(String userId);
 List<EmploymentContract> findAllByOrderByStartDateDesc();
 boolean existsByUserIdAndContractTypeAndStatusIn(String userId,String contractType,Collection<String> statuses);
}
