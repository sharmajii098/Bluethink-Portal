package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.ExpenseClaim;
import java.util.*;
public interface ExpenseService {
 List<ExpenseClaim> my(String userId); List<ExpenseClaim> all(); ExpenseClaim create(ExpenseClaim e,String userId,String name); ExpenseClaim update(Long id,ExpenseClaim e,String userId); ExpenseClaim review(Long id,String status,String comment,String reviewer); void delete(Long id,String userId); ExpenseClaim get(Long id);
}
