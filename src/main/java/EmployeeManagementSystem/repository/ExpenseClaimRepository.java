package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim,Long> {
 List<ExpenseClaim> findByUserIdOrderByExpenseDateDesc(String userId);
 List<ExpenseClaim> findAllByOrderByExpenseDateDesc();
 List<ExpenseClaim> findByStatusOrderByExpenseDateDesc(String status);
}
