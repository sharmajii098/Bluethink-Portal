package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.LeaveEncashmentRequest;
import java.util.List;

public interface LeaveEncashmentService {
    List<LeaveEncashmentRequest> my(String userId);
    List<LeaveEncashmentRequest> all();
    LeaveEncashmentRequest create(LeaveEncashmentRequest request, String userId, String employeeName);
    LeaveEncashmentRequest review(Long id, String status, String comment, String reviewerUserId);
}
