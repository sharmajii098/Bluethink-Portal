package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeDataChangeRequest; import java.util.*;
public interface EmployeeDataChangeRequestService{List<EmployeeDataChangeRequest> my(String u);List<EmployeeDataChangeRequest> all();EmployeeDataChangeRequest create(EmployeeDataChangeRequest x,String u,String n);EmployeeDataChangeRequest review(Long id,String status,String comment,String reviewer);}
