package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeRecognition; import EmployeeManagementSystem.repository.*; import org.springframework.stereotype.Service; import java.time.LocalDateTime; import java.util.*;
@Service public class EmployeeRecognitionServiceImpl implements EmployeeRecognitionService{
 private final EmployeeRecognitionRepository repo; private final EmployeeRepository employees;
 public EmployeeRecognitionServiceImpl(EmployeeRecognitionRepository r,EmployeeRepository e){repo=r;employees=e;}
 public List<EmployeeRecognition> my(String u){return repo.findByNomineeUserIdOrNominatedByUserIdOrderByCreatedAtDesc(u,u);}
 public List<EmployeeRecognition> all(){return repo.findAllByOrderByCreatedAtDesc();}
 public EmployeeRecognition nominate(EmployeeRecognition x,String u,String n){if(x.getNomineeUserId()==null||x.getNomineeUserId().isBlank())throw new IllegalArgumentException("Nominee user ID is required."); if(x.getNomineeUserId().equals(u))throw new IllegalArgumentException("You cannot nominate yourself."); String nn=employees.findByRegisterEmployee_UserId(x.getNomineeUserId()).map(e->e.getFullName()).orElse(x.getNomineeUserId()); x.setId(null);x.setRecognitionNumber("REC-"+System.currentTimeMillis());x.setNominatedByUserId(u);x.setNominatedByName(n);x.setNomineeName(nn);x.setStatus("PENDING");x.setReviewedAt(null);x.setReviewerUserId(null);x.setReviewComment(null); if(x.getPoints()==null||x.getPoints()<0)x.setPoints(0); return repo.save(x);}
 public EmployeeRecognition review(Long id,String s,String c,String r){EmployeeRecognition x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Recognition not found."));if(!Set.of("APPROVED","REJECTED").contains(s))throw new IllegalArgumentException("Status must be APPROVED or REJECTED.");x.setStatus(s);x.setReviewerUserId(r);x.setReviewComment(c);x.setReviewedAt(LocalDateTime.now());return repo.save(x);}
}
