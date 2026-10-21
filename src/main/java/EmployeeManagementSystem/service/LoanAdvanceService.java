package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*; import java.util.*;
public interface LoanAdvanceService { List<LoanAdvance> list(String userId, boolean all); LoanAdvance create(LoanAdvance x,String userId,String name); LoanAdvance update(Long id,LoanAdvance x,String userId); LoanAdvance review(Long id,String status,String comment,String reviewer); List<LoanRepayment> schedule(Long id); LoanRepayment markPaid(Long loanId,Long repaymentId); void delete(Long id,String userId); }
