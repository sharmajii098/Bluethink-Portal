package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeEmergencyContact;
import java.util.*;
public interface EmployeeEmergencyContactService{
 List<EmployeeEmergencyContact> my(String userId); List<EmployeeEmergencyContact> all();
 EmployeeEmergencyContact create(EmployeeEmergencyContact x,String userId,String employeeName);
 EmployeeEmergencyContact update(Long id,EmployeeEmergencyContact x,String userId,boolean management);
 void delete(Long id,String userId,boolean management);
 EmployeeEmergencyContact verify(Long id,String reviewerUserId);
}
