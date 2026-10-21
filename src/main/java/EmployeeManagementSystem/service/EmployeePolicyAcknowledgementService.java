package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeePolicyAcknowledgement;
import java.util.*;
public interface EmployeePolicyAcknowledgementService {
 List<EmployeePolicyAcknowledgement> my(String userId);
 List<EmployeePolicyAcknowledgement> all();
 EmployeePolicyAcknowledgement create(EmployeePolicyAcknowledgement x,String userId,String employeeName);
 EmployeePolicyAcknowledgement acknowledge(Long id,String userId,String note);
 EmployeePolicyAcknowledgement updateStatus(Long id,String status,String reviewer);
}
