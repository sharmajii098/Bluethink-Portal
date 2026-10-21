package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeAccessCardRequest;
import java.util.*;
public interface EmployeeAccessCardRequestService{
 List<EmployeeAccessCardRequest> my(String userId); List<EmployeeAccessCardRequest> all();
 EmployeeAccessCardRequest create(EmployeeAccessCardRequest x,String userId,String name);
 EmployeeAccessCardRequest review(Long id,String reviewer,String status,String comment);
}
