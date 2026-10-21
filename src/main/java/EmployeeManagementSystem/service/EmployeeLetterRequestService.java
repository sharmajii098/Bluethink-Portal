package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeLetterRequest;
import java.util.*;
public interface EmployeeLetterRequestService { List<EmployeeLetterRequest> all(); List<EmployeeLetterRequest> my(String userId); EmployeeLetterRequest create(EmployeeLetterRequest x,String userId,String name); EmployeeLetterRequest review(Long id,Map<String,Object> body,String reviewerId,String reviewerName); }
