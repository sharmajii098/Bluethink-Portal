package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeTransportRequest; import java.util.*;
public interface EmployeeTransportRequestService { List<EmployeeTransportRequest> my(String userId); List<EmployeeTransportRequest> all(); EmployeeTransportRequest create(EmployeeTransportRequest request,String userId,String employeeName); EmployeeTransportRequest review(Long id,String status,String comment,String reviewerUserId,String assignedTo); }
