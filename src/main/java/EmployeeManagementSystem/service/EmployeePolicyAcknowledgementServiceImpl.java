package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeePolicyAcknowledgement;
import EmployeeManagementSystem.repository.EmployeePolicyAcknowledgementRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class EmployeePolicyAcknowledgementServiceImpl implements EmployeePolicyAcknowledgementService{
 private final EmployeePolicyAcknowledgementRepository repo;
 public EmployeePolicyAcknowledgementServiceImpl(EmployeePolicyAcknowledgementRepository r){repo=r;}
 public List<EmployeePolicyAcknowledgement> my(String u){return repo.findByUserIdOrderByDueDateAscCreatedAtDesc(u);}
 public List<EmployeePolicyAcknowledgement> all(){return repo.findAllByOrderByDueDateAscCreatedAtDesc();}
 private void validate(EmployeePolicyAcknowledgement x){
  if(x.getTitle()==null||x.getTitle().isBlank())throw new IllegalArgumentException("Policy title is required.");
  if(x.getTitle().length()>200)throw new IllegalArgumentException("Policy title must be 200 characters or less.");
  if(x.getDescription()!=null&&x.getDescription().length()>4000)throw new IllegalArgumentException("Description must be 4000 characters or less.");
  if(x.getVersion()==null||x.getVersion().isBlank())throw new IllegalArgumentException("Policy version is required.");
 }
 public EmployeePolicyAcknowledgement create(EmployeePolicyAcknowledgement x,String u,String n){validate(x);if(repo.existsByUserIdAndTitleAndVersion(u,x.getTitle().trim(),x.getVersion().trim()))throw new IllegalArgumentException("This policy version is already assigned to the employee.");x.setId(null);x.setAcknowledgementNumber("ACK-"+System.currentTimeMillis()+"-"+(int)(Math.random()*1000));x.setUserId(u);x.setEmployeeName(n);x.setTitle(x.getTitle().trim());x.setVersion(x.getVersion().trim());x.setStatus("PENDING");x.setAcknowledgedAt(null);x.setAcknowledgementNote(null);return repo.save(x);}
 public EmployeePolicyAcknowledgement acknowledge(Long id,String u,String note){EmployeePolicyAcknowledgement e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Acknowledgement not found."));if(!e.getUserId().equals(u))throw new SecurityException("You can acknowledge only your own policy assignments.");if("ACKNOWLEDGED".equals(e.getStatus()))return e;if(e.getDueDate()!=null&&e.getDueDate().isBefore(java.time.LocalDate.now()))throw new IllegalArgumentException("This policy acknowledgement is overdue and can no longer be acknowledged.");e.setStatus("ACKNOWLEDGED");e.setAcknowledgementNote(note);e.setAcknowledgedAt(LocalDateTime.now());return repo.save(e);}
 public EmployeePolicyAcknowledgement updateStatus(Long id,String status,String reviewer){EmployeePolicyAcknowledgement e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Acknowledgement not found."));if(!Set.of("PENDING","ACKNOWLEDGED","WAIVED").contains(status))throw new IllegalArgumentException("Invalid status.");e.setStatus(status);e.setReviewedBy(reviewer);return repo.save(e);}
}
