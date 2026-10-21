package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeSkill;
import java.util.List;
public interface EmployeeSkillService { List<EmployeeSkill> my(String u); List<EmployeeSkill> all(); EmployeeSkill create(EmployeeSkill x,String u,String n); EmployeeSkill update(Long id,EmployeeSkill x,String u,boolean mgmt); void delete(Long id,String u,boolean mgmt); EmployeeSkill review(Long id,String u,String status,String comment); }
