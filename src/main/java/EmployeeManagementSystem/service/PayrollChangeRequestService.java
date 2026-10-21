package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.PayrollChangeRequest; import java.util.*;
public interface PayrollChangeRequestService { List<PayrollChangeRequest> my(String userId); List<PayrollChangeRequest> all(); PayrollChangeRequest create(PayrollChangeRequest x,String userId,String name); PayrollChangeRequest review(Long id,String status,String comment,String reviewer); }
