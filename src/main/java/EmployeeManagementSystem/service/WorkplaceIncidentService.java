package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.WorkplaceIncident; import java.util.*;
public interface WorkplaceIncidentService { List<WorkplaceIncident> my(String userId); List<WorkplaceIncident> all(); WorkplaceIncident create(WorkplaceIncident x,String userId,String name); WorkplaceIncident review(Long id,String status,String comment,String reviewer,String assignedTo,String correctiveAction); }
