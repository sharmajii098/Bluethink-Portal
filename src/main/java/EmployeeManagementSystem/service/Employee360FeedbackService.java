package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Employee360Feedback;
import java.util.*;
public interface Employee360FeedbackService{
 List<Employee360Feedback> my(String userId);
 List<Employee360Feedback> all();
 Employee360Feedback create(Employee360Feedback x,String reviewer,String reviewerName,String subjectName);
 Employee360Feedback review(Long id,String reviewer,String status,String comment);
}
