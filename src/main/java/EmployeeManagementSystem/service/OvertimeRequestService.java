package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.OvertimeRequest; import java.util.*;
public interface OvertimeRequestService { List<OvertimeRequest> my(String userId); List<OvertimeRequest> all(); OvertimeRequest create(OvertimeRequest x,String userId,String name); OvertimeRequest review(Long id,String status,String comment,String reviewer); }
