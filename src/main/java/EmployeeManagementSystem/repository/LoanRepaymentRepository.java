package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment,Long>{ List<LoanRepayment> findByLoanIdOrderByInstallmentNumber(Long loanId); }
