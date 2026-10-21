package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.LeaveDonationRequest;
import java.util.*;
public interface LeaveDonationRequestService{
    List<LeaveDonationRequest> my(String userId);
    List<LeaveDonationRequest> all();
    LeaveDonationRequest create(LeaveDonationRequest request,String donorUserId,String donorName,String recipientName);
    LeaveDonationRequest review(Long id,String status,String comment,String reviewerUserId);
    void reconcileApprovedTransfers();
}
