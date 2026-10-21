package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.HrServiceRequest;
import java.util.*;
public interface HrServiceRequestService {
 List<HrServiceRequest> my(String userId);
 List<HrServiceRequest> all();
 HrServiceRequest create(HrServiceRequest request,String userId,String employeeName);
 HrServiceRequest review(Long id,String reviewer,String status,String comment,String assignedTo,String assignedTeam,String resolution);
}
