package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.LeaveEncashmentRequest;
import EmployeeManagementSystem.repository.LeaveEncashmentRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LeaveEncashmentServiceImpl implements LeaveEncashmentService {
    private static final Set<String> ACTIVE = Set.of("SUBMITTED", "UNDER_REVIEW");
    private static final Set<String> REVIEW = Set.of("UNDER_REVIEW", "APPROVED", "REJECTED");
    private final LeaveEncashmentRequestRepository repo;

    public LeaveEncashmentServiceImpl(LeaveEncashmentRequestRepository repo){ this.repo=repo; }

    @Override public List<LeaveEncashmentRequest> my(String userId){ return repo.findByUserIdOrderByCreatedAtDesc(userId); }
    @Override public List<LeaveEncashmentRequest> all(){ return repo.findAllByOrderByCreatedAtDesc(); }

    @Override @Transactional
    public LeaveEncashmentRequest create(LeaveEncashmentRequest x, String userId, String employeeName){
        if(x==null || x.getFinancialYear()==null || x.getFinancialYear().isBlank()) throw new IllegalArgumentException("Financial year is required.");
        if(x.getLeaveType()==null || x.getLeaveType().isBlank()) throw new IllegalArgumentException("Leave type is required.");
        if(x.getRequestedDays()==null || x.getRequestedDays().compareTo(BigDecimal.ZERO)<=0) throw new IllegalArgumentException("Requested days must be greater than zero.");
        if(x.getRequestedDays().compareTo(new BigDecimal("365"))>0) throw new IllegalArgumentException("Requested days cannot exceed 365.");
        if(x.getReason()==null || x.getReason().isBlank()) throw new IllegalArgumentException("Reason is required.");
        if(repo.existsByUserIdAndFinancialYearAndLeaveTypeAndStatusIn(userId,x.getFinancialYear(),x.getLeaveType(),ACTIVE)) throw new IllegalArgumentException("An active encashment request already exists for this leave type and financial year.");
        x.setId(null); x.setUserId(userId); x.setEmployeeName(employeeName); x.setRequestNumber("LE-"+System.currentTimeMillis());
        x.setStatus("SUBMITTED"); x.setApprovedDays(BigDecimal.ZERO); x.setEstimatedAmount(BigDecimal.ZERO); x.setReviewerUserId(null); x.setReviewerComment(null); x.setReviewedAt(null);
        return repo.save(x);
    }

    @Override @Transactional
    public LeaveEncashmentRequest review(Long id, String status, String comment, String reviewerUserId){
        LeaveEncashmentRequest x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Encashment request not found."));
        if(!REVIEW.contains(status)) throw new IllegalArgumentException("Invalid review status.");
        if(Set.of("APPROVED","REJECTED").contains(x.getStatus())) throw new IllegalArgumentException("This request has already been finalized.");
        if("APPROVED".equals(status)){
            BigDecimal approved=x.getApprovedDays();
            if(approved==null || approved.compareTo(BigDecimal.ZERO)<=0) approved=x.getRequestedDays();
            if(approved.compareTo(x.getRequestedDays())>0) throw new IllegalArgumentException("Approved days cannot exceed requested days.");
            x.setApprovedDays(approved);
        }
        x.setStatus(status); x.setReviewerUserId(reviewerUserId); x.setReviewerComment(comment); x.setReviewedAt(LocalDateTime.now());
        return repo.save(x);
    }
}
