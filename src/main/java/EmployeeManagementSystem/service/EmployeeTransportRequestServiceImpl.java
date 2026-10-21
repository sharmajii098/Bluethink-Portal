package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeTransportRequest; import EmployeeManagementSystem.repository.EmployeeTransportRequestRepository; import org.springframework.stereotype.Service; import java.time.*; import java.util.*;
@Service public class EmployeeTransportRequestServiceImpl implements EmployeeTransportRequestService{
 private final EmployeeTransportRequestRepository repo; public EmployeeTransportRequestServiceImpl(EmployeeTransportRequestRepository r){repo=r;}
 public List<EmployeeTransportRequest> my(String u){return repo.findByUserIdOrderByCreatedAtDesc(u);} public List<EmployeeTransportRequest> all(){return repo.findAllByOrderByCreatedAtDesc();}
 public EmployeeTransportRequest create(EmployeeTransportRequest x,String u,String n){
  if(x.getRequestType()==null||!Set.of("SHUTTLE","CAB","PARKING","TRANSPORT_ALLOWANCE").contains(x.getRequestType().toUpperCase())) throw new IllegalArgumentException("Valid transport request type is required.");
  if(x.getEffectiveDate()!=null&&x.getEndDate()!=null&&x.getEndDate().isBefore(x.getEffectiveDate())) throw new IllegalArgumentException("End date cannot be before effective date.");
  if(x.getEffectiveDate()!=null&&x.getEffectiveDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Effective date cannot be in the past.");
  String p=Optional.ofNullable(x.getPriority()).orElse("MEDIUM").toUpperCase(); if(!Set.of("LOW","MEDIUM","HIGH","URGENT").contains(p)) throw new IllegalArgumentException("Invalid priority.");
  if(repo.existsByUserIdAndRequestTypeAndStatusIn(u,x.getRequestType().toUpperCase(),List.of("SUBMITTED","UNDER_REVIEW","APPROVED"))) throw new IllegalArgumentException("An active request already exists for this transport type.");
  x.setId(null); x.setRequestNumber("TRN-"+System.currentTimeMillis()); x.setUserId(u); x.setEmployeeName(n); x.setRequestType(x.getRequestType().toUpperCase()); x.setPriority(p); x.setStatus("SUBMITTED"); return repo.save(x);
 }
 public EmployeeTransportRequest review(Long id,String s,String c,String r,String a){EmployeeTransportRequest x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Transport request not found.")); if(!Set.of("UNDER_REVIEW","APPROVED","REJECTED","CANCELLED").contains(s)) throw new IllegalArgumentException("Invalid request status."); x.setStatus(s);x.setReviewerUserId(r);x.setReviewerComment(c);x.setAssignedTo(a);x.setReviewedAt(LocalDateTime.now());return repo.save(x);}
}
