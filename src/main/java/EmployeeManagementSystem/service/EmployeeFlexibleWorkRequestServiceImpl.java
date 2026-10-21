package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeeFlexibleWorkRequest;
import EmployeeManagementSystem.repository.EmployeeFlexibleWorkRequestRepository;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service
public class EmployeeFlexibleWorkRequestServiceImpl implements EmployeeFlexibleWorkRequestService {
 private final EmployeeFlexibleWorkRequestRepository repo;
 public EmployeeFlexibleWorkRequestServiceImpl(EmployeeFlexibleWorkRequestRepository repo){this.repo=repo;}
 public List<EmployeeFlexibleWorkRequest> my(String u){return repo.findByUserIdOrderByCreatedAtDesc(u);}
 public List<EmployeeFlexibleWorkRequest> all(){return repo.findAllByOrderByCreatedAtDesc();}
 public EmployeeFlexibleWorkRequest create(EmployeeFlexibleWorkRequest x,String u,String n){
  String t=Optional.ofNullable(x.getArrangementType()).orElse("").trim().toUpperCase();
  if(!Set.of("HYBRID","REMOTE","FLEXIBLE_HOURS","COMPRESSED_WEEK","CUSTOM").contains(t)) throw new IllegalArgumentException("Valid work arrangement type is required.");
  if(x.getEffectiveDate()==null) throw new IllegalArgumentException("Effective date is required.");
  if(x.getEffectiveDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Effective date cannot be in the past.");
  if(x.getEndDate()!=null&&x.getEndDate().isBefore(x.getEffectiveDate())) throw new IllegalArgumentException("End date cannot be before effective date.");
  if(x.getReason()==null||x.getReason().isBlank()) throw new IllegalArgumentException("Reason is required.");
  if(repo.existsByUserIdAndStatusIn(u,List.of("SUBMITTED","UNDER_REVIEW","APPROVED"))) throw new IllegalArgumentException("An active flexible-work request already exists.");
  x.setId(null);x.setRequestNumber("FWA-"+System.currentTimeMillis());x.setUserId(u);x.setEmployeeName(n);x.setArrangementType(t);x.setStatus("SUBMITTED");return repo.save(x);
 }
 public EmployeeFlexibleWorkRequest review(Long id,String s,String c,String r){
  if(!Set.of("UNDER_REVIEW","APPROVED","REJECTED","CANCELLED").contains(s)) throw new IllegalArgumentException("Invalid request status.");
  EmployeeFlexibleWorkRequest x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Flexible work request not found."));
  x.setStatus(s);x.setReviewerUserId(r);x.setReviewerComment(c);x.setReviewedAt(LocalDateTime.now());return repo.save(x);
 }
}
