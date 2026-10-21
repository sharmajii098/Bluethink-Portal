package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeComplianceRecord;
import java.util.*;
public interface EmployeeComplianceRecordService{
 List<EmployeeComplianceRecord> my(String userId); List<EmployeeComplianceRecord> all();
 EmployeeComplianceRecord create(EmployeeComplianceRecord x,String userId,String name);
 EmployeeComplianceRecord update(Long id,EmployeeComplianceRecord x,String userId,boolean mgmt);
 void delete(Long id,String userId,boolean mgmt);
 EmployeeComplianceRecord review(Long id,String userId,String status,String comment);
}
