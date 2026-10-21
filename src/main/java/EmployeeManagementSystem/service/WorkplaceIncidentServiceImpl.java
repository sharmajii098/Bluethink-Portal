package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.WorkplaceIncident; import EmployeeManagementSystem.repository.WorkplaceIncidentRepository; import org.springframework.stereotype.Service; import java.time.*; import java.util.*;
@Service public class WorkplaceIncidentServiceImpl implements WorkplaceIncidentService{
 private final WorkplaceIncidentRepository repo; public WorkplaceIncidentServiceImpl(WorkplaceIncidentRepository r){repo=r;}
 public List<WorkplaceIncident> my(String u){return repo.findByReporterUserIdOrderByCreatedAtDesc(u);} public List<WorkplaceIncident> all(){return repo.findAllByOrderByCreatedAtDesc();}
 public WorkplaceIncident create(WorkplaceIncident x,String u,String n){
  if(x.getIncidentType()==null||!Set.of("SAFETY","INJURY","NEAR_MISS","PROPERTY_DAMAGE","SECURITY","HARASSMENT","OTHER").contains(x.getIncidentType().toUpperCase())) throw new IllegalArgumentException("Valid incident type is required.");
  if(x.getSeverity()==null||!Set.of("LOW","MEDIUM","HIGH","CRITICAL").contains(x.getSeverity().toUpperCase())) throw new IllegalArgumentException("Valid severity is required.");
  if(x.getIncidentDate()==null) throw new IllegalArgumentException("Incident date is required."); if(x.getIncidentDate().isAfter(LocalDate.now())) throw new IllegalArgumentException("Incident date cannot be in the future.");
  if(x.getLocation()==null||x.getLocation().isBlank()) throw new IllegalArgumentException("Incident location is required."); if(x.getDescription()==null||x.getDescription().isBlank()) throw new IllegalArgumentException("Incident description is required.");
  x.setId(null); x.setIncidentNumber("INC-"+System.currentTimeMillis()); x.setReporterUserId(u); x.setReporterName(n); x.setIncidentType(x.getIncidentType().toUpperCase()); x.setSeverity(x.getSeverity().toUpperCase()); x.setStatus("REPORTED"); return repo.save(x);
 }
 public WorkplaceIncident review(Long id,String s,String c,String r,String a,String corrective){WorkplaceIncident x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Incident not found.")); if(!Set.of("UNDER_REVIEW","ACTION_REQUIRED","RESOLVED","CLOSED").contains(s)) throw new IllegalArgumentException("Invalid incident status."); x.setStatus(s);x.setReviewerUserId(r);x.setReviewerComment(c);x.setAssignedTo(a);x.setCorrectiveAction(corrective);x.setReviewedAt(LocalDateTime.now());if("RESOLVED".equals(s)||"CLOSED".equals(s))x.setResolvedAt(LocalDateTime.now());return repo.save(x);}
}
