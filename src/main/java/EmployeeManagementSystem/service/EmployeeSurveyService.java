package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*; import java.util.*;
public interface EmployeeSurveyService { List<EmployeeSurvey> all(); List<EmployeeSurvey> active(); EmployeeSurvey create(EmployeeSurvey s); EmployeeSurvey update(Long id,EmployeeSurvey s); EmployeeSurvey publish(Long id); EmployeeSurvey close(Long id); EmployeeSurveyResponse respond(Long id,String userId,String name,Integer rating,String comment); List<EmployeeSurveyResponse> responses(Long id); Optional<EmployeeSurveyResponse> myResponse(Long id,String userId); }
