package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeReferral; import java.util.*;
public interface EmployeeReferralService{List<EmployeeReferral> my(String u);List<EmployeeReferral> all();EmployeeReferral create(EmployeeReferral x,String u,String n);EmployeeReferral review(Long id,String status,String comment,String reviewer);}
