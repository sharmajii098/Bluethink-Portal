package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeeFlexibleWorkRequest;
import java.util.List;

public interface EmployeeFlexibleWorkRequestService {
 List<EmployeeFlexibleWorkRequest> my(String userId);
 List<EmployeeFlexibleWorkRequest> all();
 EmployeeFlexibleWorkRequest create(EmployeeFlexibleWorkRequest request,String userId,String employeeName);
 EmployeeFlexibleWorkRequest review(Long id,String status,String comment,String reviewerUserId);
}
