package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service @Transactional
public class ProbationReviewServiceImpl implements ProbationReviewService {
 private final ProbationReviewRepository repo; private final EmployeeRepository employees;
 public ProbationReviewServiceImpl(ProbationReviewRepository r,EmployeeRepository e){repo=r;employees=e;}
 public List<ProbationReview> all(){return repo.findAllByOrderByProbationEndDateAsc();}
 public List<ProbationReview> my(String u){return repo.findByEmployeeUserIdOrderByCreatedAtDesc(u);}
 public ProbationReview create(ProbationReview x,String u,String name){
  if(repo.findTopByEmployeeUserIdOrderByCreatedAtDesc(u).filter(r->Set.of("PENDING","UNDER_REVIEW").contains(r.getStatus())).isPresent()) throw new IllegalStateException("An active probation review already exists.");
  Employee e=employees.findByRegisterEmployee_UserId(u).orElseThrow(()->new IllegalStateException("Employee not found."));
  x.setEmployeeUserId(u);x.setEmployeeName(name);x.setEmployeeId(e.getId());x.setJoiningDate(e.getJoiningDate());
  if(x.getProbationEndDate()==null && e.getJoiningDate()!=null)x.setProbationEndDate(e.getJoiningDate().plusMonths(6));
  if(e.getDepartment()!=null)x.setDepartmentName(e.getDepartment().getDepartmentName());
  if(e.getEmployeeProfile()!=null)x.setCurrentDesignation(e.getEmployeeProfile().getDesignation());
  x.setStatus("PENDING");x.setReviewNumber("PRB-"+String.format("%06d",new Random().nextInt(900000)+100000));
  return repo.save(x);
 }
 public ProbationReview review(Long id,String status,String decision,Integer rating,String comment,String reviewer){
  ProbationReview x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Review not found."));
  if(!Set.of("UNDER_REVIEW","CONFIRMED","EXTENDED","NOT_CONFIRMED").contains(status)) throw new IllegalArgumentException("Invalid review status.");
  x.setStatus(status);x.setDecision(decision);x.setRating(rating);x.setReviewerUserId(reviewer);x.setReviewerComment(comment);x.setDecisionDate(LocalDate.now());
  if("CONFIRMED".equals(status)) x.setConfirmationDate(LocalDate.now());
  return repo.save(x);
 }
}
