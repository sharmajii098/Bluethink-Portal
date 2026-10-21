package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeBeneficiary;
import java.util.*;
public interface EmployeeBeneficiaryService{
 List<EmployeeBeneficiary> my(String userId); List<EmployeeBeneficiary> all();
 EmployeeBeneficiary create(EmployeeBeneficiary x,String userId,String employeeName);
 EmployeeBeneficiary update(Long id,EmployeeBeneficiary x,String userId,boolean management);
 void delete(Long id,String userId,boolean management);
 EmployeeBeneficiary review(Long id,String status,String comment,String reviewerUserId);
}
