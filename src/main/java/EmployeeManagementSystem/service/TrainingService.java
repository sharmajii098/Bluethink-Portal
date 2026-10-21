package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.TrainingCourse;
import java.util.*;
public interface TrainingService { List<Map<String,Object>> catalog(String userId); List<Map<String,Object>> my(String userId); List<TrainingCourse> all(); TrainingCourse save(TrainingCourse c); void delete(Long id); Map<String,Object> enroll(Long id,String userId,String employeeName); Map<String,Object> progress(Long id,String userId,int percent); Map<String,Object> feedback(Long id,String userId,String text); List<Map<String,Object>> compliance(); }
