package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.ManagerOneOnOne;
import EmployeeManagementSystem.repository.ManagerOneOnOneRepository;
import EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.*;
@Service
public class ManagerOneOnOneServiceImpl implements ManagerOneOnOneService{
 private final ManagerOneOnOneRepository repo;
 private final EmployeeRepository employees;
 public ManagerOneOnOneServiceImpl(ManagerOneOnOneRepository r, EmployeeRepository e){repo=r;employees=e;}
 public List<ManagerOneOnOne> my(String u){return repo.findByManagerUserIdOrEmployeeUserIdOrderByMeetingDateDesc(u,u);}
 public List<ManagerOneOnOne> all(){return repo.findAllByOrderByMeetingDateDesc();}
 private void validate(ManagerOneOnOne x){
  if(x.getMeetingDate()==null) throw new IllegalArgumentException("Meeting date is required.");
  if(x.getEmployeeUserId()==null||x.getEmployeeUserId().isBlank()) throw new IllegalArgumentException("Employee user ID is required.");
  if(x.getMeetingDate().isBefore(LocalDate.now().minusYears(1))) throw new IllegalArgumentException("Meeting date is too far in the past.");
  if(x.getAgenda()!=null&&x.getAgenda().length()>2000) throw new IllegalArgumentException("Agenda is too long.");
 }
 public ManagerOneOnOne create(ManagerOneOnOne x,String manager,String name){
  validate(x);
  String employeeUserId=x.getEmployeeUserId().trim();
  var employee=employees.findByRegisterEmployee_UserId(employeeUserId)
    .orElseGet(() -> employees.findByEmployeeProfile_UserId(employeeUserId).orElse(null));
  if(employee==null) throw new IllegalArgumentException("Employee user ID not found: "+employeeUserId);
  if(manager.equals(employeeUserId)) throw new IllegalArgumentException("Manager and employee must be different.");
  if(repo.existsByManagerUserIdAndEmployeeUserIdAndMeetingDateAndStatusIn(manager,employeeUserId,x.getMeetingDate(),Set.of("SCHEDULED","RESCHEDULED"))) throw new IllegalArgumentException("An active 1:1 already exists for this employee and date.");
  x.setId(null); x.setEmployeeUserId(employeeUserId); x.setEmployeeName(employee.getFullName()); x.setMeetingNumber("1ON1-"+System.currentTimeMillis()); x.setManagerUserId(manager); x.setManagerName(name); x.setStatus("SCHEDULED"); return repo.save(x);
 }
 public ManagerOneOnOne update(Long id,ManagerOneOnOne x,String user,String role){
  ManagerOneOnOne e=repo.findById(id).orElseThrow(()->new NoSuchElementException("1:1 meeting not found."));
  boolean mgmt=Set.of("ADMIN","HR","MANAGER").contains(role); if(!mgmt) throw new SecurityException("Only management can update a 1:1 meeting.");
  if(x.getMeetingDate()!=null)e.setMeetingDate(x.getMeetingDate()); if(x.getMeetingTime()!=null)e.setMeetingTime(x.getMeetingTime()); if(x.getLocationOrLink()!=null)e.setLocationOrLink(x.getLocationOrLink());
  if(x.getAgenda()!=null)e.setAgenda(x.getAgenda()); if(x.getDiscussionNotes()!=null)e.setDiscussionNotes(x.getDiscussionNotes()); if(x.getActionItems()!=null)e.setActionItems(x.getActionItems()); if(x.getNextFollowUpDate()!=null)e.setNextFollowUpDate(x.getNextFollowUpDate());
  if(x.getStatus()!=null&&Set.of("SCHEDULED","RESCHEDULED","COMPLETED","CANCELLED").contains(x.getStatus().toUpperCase()))e.setStatus(x.getStatus().toUpperCase());
  if(x.getCancellationReason()!=null)e.setCancellationReason(x.getCancellationReason()); return repo.save(e);
 }
}
