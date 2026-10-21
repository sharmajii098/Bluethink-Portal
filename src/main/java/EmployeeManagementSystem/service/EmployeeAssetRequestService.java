package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeAssetRequest; import java.util.*;
public interface EmployeeAssetRequestService { List<EmployeeAssetRequest> my(String userId); List<EmployeeAssetRequest> all(); EmployeeAssetRequest create(EmployeeAssetRequest request,String userId,String employeeName); EmployeeAssetRequest review(Long id,String status,String comment,String reviewerUserId,String assignedTo,String assetReference); }
