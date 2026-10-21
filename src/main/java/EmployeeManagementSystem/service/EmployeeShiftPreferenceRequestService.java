package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeShiftPreferenceRequest; import java.util.*;
public interface EmployeeShiftPreferenceRequestService { List<EmployeeShiftPreferenceRequest> my(String userId); List<EmployeeShiftPreferenceRequest> all(); EmployeeShiftPreferenceRequest create(EmployeeShiftPreferenceRequest x,String userId,String name); EmployeeShiftPreferenceRequest review(Long id,String status,String comment,String reviewer); }
