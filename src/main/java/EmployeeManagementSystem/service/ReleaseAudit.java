package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Activity;
import EmployeeManagementSystem.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ReleaseAudit {
 private final ActivityRepository records; private final PortalActor actor;
 public ReleaseAudit(ActivityRepository records,PortalActor actor){this.records=records;this.actor=actor;}
 @Transactional public void record(String module,String action,Object id){
  Activity row=new Activity();row.setUserId(actor.id());row.setEmployeeName(actor.id());
  row.setRole(actor.has("ADMIN")?"ADMIN":actor.has("HR")?"HR":actor.has("MANAGER")?"MANAGER":"EMPLOYEE");
  row.setModule(module);row.setAction(action);row.setActivity(module+" "+action);row.setStatus("SUCCESS");
  row.setDetails("Record ID: "+id);records.save(row);
 }
}
