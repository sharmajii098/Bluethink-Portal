package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmploymentContract; import java.util.*;
public interface EmploymentContractService { List<EmploymentContract> my(String u); List<EmploymentContract> all(); EmploymentContract create(EmploymentContract x,String u,String n); EmploymentContract update(Long id,EmploymentContract x,String u,boolean mgmt); void delete(Long id,String u,boolean mgmt); EmploymentContract review(Long id,String status,String comment,String reviewer); }
