package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.ExpenseClaim;
import EmployeeManagementSystem.repository.ExpenseClaimRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; import java.util.*;
@Service
public class ExpenseServiceImpl implements ExpenseService {
 private final ExpenseClaimRepository repo; public ExpenseServiceImpl(ExpenseClaimRepository r){repo=r;}
 public List<ExpenseClaim> my(String u){return repo.findByUserIdOrderByExpenseDateDesc(u);} public List<ExpenseClaim> all(){return repo.findAllByOrderByExpenseDateDesc();}
 public ExpenseClaim create(ExpenseClaim e,String u,String n){e.setId(null);e.setUserId(u);e.setEmployeeName(n);e.setStatus("PENDING");return repo.save(e);}
 public ExpenseClaim update(Long id,ExpenseClaim x,String u){ExpenseClaim e=get(id);if(!e.getUserId().equals(u)||!("PENDING".equals(e.getStatus())||"REJECTED".equals(e.getStatus())))throw new IllegalArgumentException("Only your pending or rejected claims can be edited."); e.setCategory(x.getCategory());e.setAmount(x.getAmount());e.setExpenseDate(x.getExpenseDate());e.setDescription(x.getDescription());e.setReceiptName(x.getReceiptName());return repo.save(e);}
 public ExpenseClaim review(Long id,String status,String comment,String reviewer){if(!Set.of("APPROVED","REJECTED","PAID").contains(status))throw new IllegalArgumentException("Invalid expense status.");ExpenseClaim e=get(id);if(!"PENDING".equals(e.getStatus())&&!("APPROVED".equals(e.getStatus())&&"PAID".equals(status)))throw new IllegalArgumentException("Only pending claims can be reviewed, and approved claims can be marked paid.");e.setStatus(status);e.setApproverComment(comment);e.setApprovedBy(reviewer);e.setReviewedAt(LocalDateTime.now());return repo.save(e);}
 public void delete(Long id,String u){ExpenseClaim e=get(id);if(!e.getUserId().equals(u)||!"PENDING".equals(e.getStatus()))throw new IllegalArgumentException("Only your pending claims can be deleted.");repo.delete(e);} public ExpenseClaim get(Long id){return repo.findById(id).orElseThrow(()->new NoSuchElementException("Expense claim not found."));}
}
