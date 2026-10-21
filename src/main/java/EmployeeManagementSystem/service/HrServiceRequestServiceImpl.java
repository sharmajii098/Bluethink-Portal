package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.HrServiceRequest;
import EmployeeManagementSystem.repository.HrServiceRequestRepository;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service
public class HrServiceRequestServiceImpl implements HrServiceRequestService{
 private final HrServiceRequestRepository repo;
 public HrServiceRequestServiceImpl(HrServiceRequestRepository r){repo=r;}
 public List<HrServiceRequest> my(String u){return repo.findByUserIdOrderByCreatedAtDesc(u);}
 public List<HrServiceRequest> all(){return repo.findAllByOrderByCreatedAtDesc();}
 private void validate(HrServiceRequest x){
  if(x.getCategory()==null||x.getCategory().isBlank()) throw new IllegalArgumentException("Category is required.");
  if(x.getSubject()==null||x.getSubject().isBlank()) throw new IllegalArgumentException("Subject is required.");
  if(x.getDescription()==null||x.getDescription().isBlank()) throw new IllegalArgumentException("Description is required.");
  if(x.getPriority()==null||!Set.of("LOW","MEDIUM","HIGH","URGENT").contains(x.getPriority().toUpperCase())) throw new IllegalArgumentException("Invalid priority.");
 }
 public HrServiceRequest create(HrServiceRequest x,String u,String n){
  validate(x); String p=x.getPriority().toUpperCase();
  if(repo.existsByUserIdAndSubjectIgnoreCaseAndStatusIn(u,x.getSubject(),Set.of("SUBMITTED","UNDER_REVIEW","ACTION_REQUIRED"))) throw new IllegalArgumentException("An active request with the same subject already exists.");
  x.setId(null); x.setRequestNumber("HR-"+System.currentTimeMillis()); x.setUserId(u); x.setEmployeeName(n); x.setPriority(p); x.setStatus("SUBMITTED");
  int hours=switch(p){case "URGENT"->8;case "HIGH"->24;case "LOW"->72;default->48;};
  x.setSlaDueAt(LocalDateTime.now().plusHours(hours)); x.setReviewerUserId(null); x.setReviewedAt(null); return repo.save(x);
 }
 public HrServiceRequest review(Long id,String reviewer,String status,String comment,String assignedTo,String assignedTeam,String resolution){
  HrServiceRequest x=repo.findById(id).orElseThrow(()->new NoSuchElementException("HR service request not found."));
  if(!Set.of("UNDER_REVIEW","ACTION_REQUIRED","RESOLVED","CLOSED","REJECTED").contains(status)) throw new IllegalArgumentException("Invalid status.");
  x.setStatus(status); x.setReviewerUserId(reviewer); x.setReviewerComment(comment); x.setAssignedTo(assignedTo); x.setAssignedTeam(assignedTeam);
  if(resolution!=null&&!resolution.isBlank()) x.setResolution(resolution);
  x.setReviewedAt(LocalDateTime.now()); if(Set.of("RESOLVED","CLOSED").contains(status)) x.setResolvedAt(LocalDateTime.now());
  return repo.save(x);
 }
}
