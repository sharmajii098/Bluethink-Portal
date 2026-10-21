package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeAvailability;
import EmployeeManagementSystem.repository.EmployeeAvailabilityRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class EmployeeAvailabilityServiceImpl implements EmployeeAvailabilityService{
 private final EmployeeAvailabilityRepository repo;
 public EmployeeAvailabilityServiceImpl(EmployeeAvailabilityRepository r){repo=r;}
 public List<EmployeeAvailability> my(String u){return repo.findByUserIdOrderByStartDateDesc(u);}
 public List<EmployeeAvailability> all(){return repo.findAllByOrderByStartDateDesc();}
 private void validate(EmployeeAvailability x){
  if(x.getStartDate()==null||x.getEndDate()==null) throw new IllegalArgumentException("Start and end dates are required.");
  if(x.getEndDate().isBefore(x.getStartDate())) throw new IllegalArgumentException("End date cannot be before start date.");
  if(x.getAvailabilityType()==null||!Set.of("OUT_OF_OFFICE","REMOTE","AVAILABLE","UNAVAILABLE").contains(x.getAvailabilityType().toUpperCase())) throw new IllegalArgumentException("Invalid availability type.");
  if(x.getReason()!=null&&x.getReason().length()>500) throw new IllegalArgumentException("Reason must be 500 characters or less.");
  if(x.getMessage()!=null&&x.getMessage().length()>1000) throw new IllegalArgumentException("Message must be 1000 characters or less.");
 }
 public EmployeeAvailability create(EmployeeAvailability x,String u,String n){
  validate(x);
  if(repo.existsByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIn(u,x.getEndDate(),x.getStartDate(),Set.of("SUBMITTED","UNDER_REVIEW","APPROVED"))) throw new IllegalArgumentException("An active availability record already overlaps these dates.");
  x.setId(null); x.setAvailabilityNumber("AVL-"+System.currentTimeMillis()); x.setUserId(u); x.setEmployeeName(n); x.setAvailabilityType(x.getAvailabilityType().toUpperCase()); x.setStatus("SUBMITTED"); x.setReviewerUserId(null); x.setReviewerComment(null); x.setReviewedAt(null); return repo.save(x);
 }
 public EmployeeAvailability review(Long id,String reviewer,String status,String comment){
  EmployeeAvailability x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Availability record not found."));
  if(!Set.of("UNDER_REVIEW","APPROVED","REJECTED","CANCELLED").contains(status)) throw new IllegalArgumentException("Invalid review status.");
  x.setStatus(status); x.setReviewerUserId(reviewer); x.setReviewerComment(comment); x.setReviewedAt(LocalDateTime.now()); return repo.save(x);
 }
}
