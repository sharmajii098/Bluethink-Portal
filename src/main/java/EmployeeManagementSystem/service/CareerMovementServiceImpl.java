package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.CareerMovementRequest; import EmployeeManagementSystem.repository.*; import org.springframework.stereotype.Service; import java.time.LocalDateTime; import java.util.*;
@Service public class CareerMovementServiceImpl implements CareerMovementService{
 private final CareerMovementRequestRepository repo; private final EmployeeRepository employees;
 public CareerMovementServiceImpl(CareerMovementRequestRepository r,EmployeeRepository e){repo=r;employees=e;}
 public List<CareerMovementRequest> my(String u){return repo.findByEmployeeUserIdOrderByCreatedAtDesc(u);} public List<CareerMovementRequest> all(){return repo.findAllByOrderByCreatedAtDesc();}
 public CareerMovementRequest create(CareerMovementRequest x,String u,String n){if(x.getProposedDesignation()==null||x.getProposedDesignation().isBlank())throw new IllegalArgumentException("Proposed designation is required."); if(x.getReason()==null||x.getReason().isBlank())throw new IllegalArgumentException("Reason is required."); x.setId(null);x.setRequestNumber("CAR-"+System.currentTimeMillis());x.setEmployeeUserId(u);x.setEmployeeName(n);x.setStatus("SUBMITTED");x.setReviewedAt(null);x.setReviewedBy(null);return repo.save(x);}
 public CareerMovementRequest review(Long id,String s,String c,String r){CareerMovementRequest x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Career request not found."));if(!Set.of("UNDER_REVIEW","APPROVED","REJECTED","COMPLETED").contains(s))throw new IllegalArgumentException("Invalid status.");x.setStatus(s);x.setHrComment(c);x.setReviewedBy(r);x.setReviewedAt(LocalDateTime.now());return repo.save(x);}
}
