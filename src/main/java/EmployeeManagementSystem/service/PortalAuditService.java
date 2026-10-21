package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Activity;
import EmployeeManagementSystem.repository.ActivityRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
@Service
public class PortalAuditService {
 private final ActivityRepository repository;
 public PortalAuditService(ActivityRepository repository){this.repository=repository;}
 public void record(Authentication auth,String module,String action,String details){
  Activity row=new Activity();row.setUserId(auth.getName());row.setEmployeeName(auth.getName());
  row.setRole(auth.getAuthorities().stream().map(a->a.getAuthority().replace("ROLE_","")).findFirst().orElse(""));
  row.setModule(module);row.setAction(action);row.setActivity(module+" "+action);row.setStatus("SUCCESS");
  row.setDetails(details.length()>1000?details.substring(0,1000):details);repository.save(row);
 }
}
