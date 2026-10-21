package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.ManagerOneOnOne;
import java.util.List;
public interface ManagerOneOnOneService{
 List<ManagerOneOnOne> my(String user); List<ManagerOneOnOne> all();
 ManagerOneOnOne create(ManagerOneOnOne x,String managerUserId,String managerName);
 ManagerOneOnOne update(Long id,ManagerOneOnOne x,String user,String role);
}
