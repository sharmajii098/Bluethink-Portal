package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeRecognition; import java.util.*;
public interface EmployeeRecognitionService { List<EmployeeRecognition> my(String userId); List<EmployeeRecognition> all(); EmployeeRecognition nominate(EmployeeRecognition x,String userId,String name); EmployeeRecognition review(Long id,String status,String comment,String reviewer); }
