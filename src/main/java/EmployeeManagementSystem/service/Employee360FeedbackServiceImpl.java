package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.Employee360Feedback;
import EmployeeManagementSystem.repository.Employee360FeedbackRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class Employee360FeedbackServiceImpl implements Employee360FeedbackService{
 private final Employee360FeedbackRepository repo;
 public Employee360FeedbackServiceImpl(Employee360FeedbackRepository r){repo=r;}
 public List<Employee360Feedback> my(String u){List<Employee360Feedback> out=new ArrayList<>(repo.findByReviewerUserIdOrderByCreatedAtDesc(u));for(Employee360Feedback x:repo.findBySubjectUserIdOrderByCreatedAtDesc(u))if(out.stream().noneMatch(y->Objects.equals(y.getId(),x.getId())))out.add(x);out.sort(Comparator.comparing(Employee360Feedback::getCreatedAt,Comparator.nullsLast(Comparator.reverseOrder())));return out;}
 public List<Employee360Feedback> all(){return repo.findAllByOrderByCreatedAtDesc();}
 private void validate(Employee360Feedback x){if(x.getSubjectUserId()==null||x.getSubjectUserId().isBlank())throw new IllegalArgumentException("Subject employee is required.");if(x.getRating()==null||x.getRating()<1||x.getRating()>5)throw new IllegalArgumentException("Rating must be between 1 and 5.");if(x.getStrengths()!=null&&x.getStrengths().length()>2000)throw new IllegalArgumentException("Strengths are too long.");if(x.getImprovementAreas()!=null&&x.getImprovementAreas().length()>2000)throw new IllegalArgumentException("Improvement areas are too long.");if(x.getComments()!=null&&x.getComments().length()>3000)throw new IllegalArgumentException("Comments are too long.");}
 public Employee360Feedback create(Employee360Feedback x,String reviewer,String reviewerName,String subjectName){validate(x);if(reviewer.equals(x.getSubjectUserId()))throw new IllegalArgumentException("You cannot submit feedback about yourself.");if(repo.existsByReviewerUserIdAndSubjectUserIdAndStatusIn(reviewer,x.getSubjectUserId(),Set.of("SUBMITTED","UNDER_REVIEW")))throw new IllegalArgumentException("An active feedback request already exists for this employee.");x.setId(null);x.setFeedbackNumber("360FB-"+System.currentTimeMillis());x.setReviewerUserId(reviewer);x.setReviewerName(reviewerName);x.setSubjectName(subjectName);x.setStatus("SUBMITTED");x.setManagementComment(null);x.setReviewedBy(null);x.setReviewedAt(null);return repo.save(x);}
 public Employee360Feedback review(Long id,String reviewer,String status,String comment){Employee360Feedback e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Feedback not found."));status=status==null?"":status.toUpperCase();if(!Set.of("UNDER_REVIEW","ACKNOWLEDGED","CLOSED","REJECTED").contains(status))throw new IllegalArgumentException("Invalid feedback status.");e.setStatus(status);e.setReviewedBy(reviewer);e.setManagementComment(comment);e.setReviewedAt(LocalDateTime.now());return repo.save(e);}
}
