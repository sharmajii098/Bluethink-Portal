package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeSuggestion;
import java.util.*;
public interface EmployeeSuggestionService{
    List<EmployeeSuggestion> my(String userId);
    List<EmployeeSuggestion> all();
    EmployeeSuggestion create(EmployeeSuggestion x,String userId,String employeeName);
    EmployeeSuggestion review(Long id,String reviewer,String status,String response);
}
