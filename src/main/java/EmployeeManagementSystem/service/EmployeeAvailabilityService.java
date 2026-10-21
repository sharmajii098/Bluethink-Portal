package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeAvailability;
import java.util.*;
public interface EmployeeAvailabilityService{
 List<EmployeeAvailability> my(String userId); List<EmployeeAvailability> all();
 EmployeeAvailability create(EmployeeAvailability x,String userId,String employeeName);
 EmployeeAvailability review(Long id,String reviewer,String status,String comment);
}
