package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.TaxDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TaxDeclarationRepository extends JpaRepository<TaxDeclaration,Long>{
    List<TaxDeclaration> findByUserIdOrderByFinancialYearDescCreatedAtDesc(String userId);
    List<TaxDeclaration> findAllByOrderByCreatedAtDesc();
    Optional<TaxDeclaration> findByUserIdAndFinancialYear(String userId,String financialYear);
}
