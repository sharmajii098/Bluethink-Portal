package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeAccessCardRequest;
import EmployeeManagementSystem.repository.EmployeeAccessCardRequestRepository;
import org.springframework.stereotype.Service;
import java.time.*; import java.util.*;
@Service public class EmployeeAccessCardRequestServiceImpl implements EmployeeAccessCardRequestService{
 private final EmployeeAccessCardRequestRepository repo; public EmployeeAccessCardRequestServiceImpl(EmployeeAccessCardRequestRepository r){repo=r;}
 public List<EmployeeAccessCardRequest> my(String u){return repo.findByUserIdOrderByCreatedAtDesc(u);} public List<EmployeeAccessCardRequest> all(){return repo.findAllByOrderByCreatedAtDesc();}
 private void validate(EmployeeAccessCardRequest x){if(x.getRequestType()==null||!Set.of("NEW","REPLACEMENT","TEMPORARY","LOST_OR_DAMAGED").contains(x.getRequestType().toUpperCase()))throw new IllegalArgumentException("Invalid request type.");if(x.getReason()==null||x.getReason().isBlank())throw new IllegalArgumentException("Reason is required.");if(x.getExpiryDate()!=null&&x.getIssueDate()!=null&&x.getExpiryDate().isBefore(x.getIssueDate()))throw new IllegalArgumentException("Expiry date cannot be before issue date.");}
 public EmployeeAccessCardRequest create(EmployeeAccessCardRequest x,String u,String n){validate(x);String t=x.getRequestType().toUpperCase();if(repo.existsByUserIdAndRequestTypeAndStatusIn(u,t,List.of("SUBMITTED","UNDER_REVIEW","APPROVED")))throw new IllegalArgumentException("An active request of this type already exists.");x.setId(null);x.setRequestNumber(null);x.setUserId(u);x.setEmployeeName(n);x.setRequestType(t);x.setStatus("SUBMITTED");x.setReviewerUserId(null);x.setReviewerComment(null);x.setReviewedAt(null);return repo.save(x);}
 public EmployeeAccessCardRequest review(Long id,String reviewer,String status,String comment){EmployeeAccessCardRequest x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Access card request not found."));if(!Set.of("UNDER_REVIEW","APPROVED","REJECTED","ISSUED","CANCELLED").contains(status))throw new IllegalArgumentException("Invalid review status.");x.setStatus(status);x.setReviewerUserId(reviewer);x.setReviewerComment(comment);x.setReviewedAt(LocalDateTime.now());if("ISSUED".equals(status)&&x.getIssueDate()==null)x.setIssueDate(LocalDate.now());return repo.save(x);}
}
