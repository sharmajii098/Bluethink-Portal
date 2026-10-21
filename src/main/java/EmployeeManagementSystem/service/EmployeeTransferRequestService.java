package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeTransferRequest; import java.util.*;
public interface EmployeeTransferRequestService { List<EmployeeTransferRequest> my(String u); List<EmployeeTransferRequest> all(); EmployeeTransferRequest create(EmployeeTransferRequest x,String u,String n); EmployeeTransferRequest review(Long id,String status,String comment,String reviewer); }
