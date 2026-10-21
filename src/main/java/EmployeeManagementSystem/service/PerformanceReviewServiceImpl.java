package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service @Transactional
public class PerformanceReviewServiceImpl implements PerformanceReviewService {
    private final PerformanceReviewRepository repo; private final EmployeeRepository employees;
    public PerformanceReviewServiceImpl(PerformanceReviewRepository r, EmployeeRepository e){repo=r;employees=e;}
    public List<PerformanceReview> list(String u,String role){
        if("ADMIN".equals(role)||"HR".equals(role)) return repo.findAllByOrderByUpdatedAtDesc();
        if("MANAGER".equals(role)){ var emp=findEmployee(u); return repo.findByEmployee_ReportingManager_IdOrderByUpdatedAtDesc(emp.getId()); }
        // Resolve the employee record first so older goals remain visible even
        // if their copied userId field was saved before the identity links were
        // normalized. The Employee relationship is the source of truth.
        var emp=findEmployee(u);
        return repo.findByEmployee_IdOrderByUpdatedAtDesc(emp.getId());
    }
    public PerformanceReview create(String u,String cycle,String goal,String due){
        if(blank(cycle)||blank(goal)) throw new IllegalArgumentException("Cycle and goal are required.");
        var e=findEmployee(u);
        var p=new PerformanceReview();
        p.setEmployee(e);
        p.setEmployeeName(e.getFullName());
        p.setUserId(u);
        p.setCycle(cycle.trim());
        p.setGoal(goal.trim());
        p.setStatus("DRAFT");
        if(!blank(due)) p.setDueDate(LocalDate.parse(due));
        return repo.save(p);
    }
    public PerformanceReview updateSelf(Long id,String u,String comment,String goal,String due){ var p=owned(id,u); if(!"DRAFT".equals(p.getStatus())&& !"REJECTED".equals(p.getStatus())) throw new IllegalStateException("Only draft or rejected goals can be edited."); if(!blank(goal)) p.setGoal(goal.trim()); p.setSelfComment(comment); if(!blank(due)) p.setDueDate(LocalDate.parse(due)); else p.setDueDate(null); return repo.save(p);}
    public PerformanceReview submit(Long id,String u){var p=owned(id,u); if(!"DRAFT".equals(p.getStatus())&& !"REJECTED".equals(p.getStatus())) throw new IllegalStateException("Only draft or rejected goals can be submitted."); if(blank(p.getGoal())) throw new IllegalStateException("Goal is required."); p.setStatus("SUBMITTED"); return repo.save(p);}
    public PerformanceReview review(Long id,String u,Integer rating,String comment){var p=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Review not found")); var mgr=findEmployee(u); if(p.getEmployee().getReportingManager()==null || !Objects.equals(p.getEmployee().getReportingManager().getId(),mgr.getId())) throw new IllegalStateException("You can review only your direct reports."); if(!"SUBMITTED".equals(p.getStatus())) throw new IllegalStateException("Only submitted goals can be reviewed."); if(rating==null||rating<1||rating>5) throw new IllegalArgumentException("Rating must be between 1 and 5."); p.setRating(rating); p.setManagerComment(comment); p.setStatus("REVIEWED"); return repo.save(p);}
    public PerformanceReview acknowledge(Long id,String u){var p=owned(id,u); if(!"REVIEWED".equals(p.getStatus())) throw new IllegalStateException("Only reviewed goals can be acknowledged."); p.setStatus("ACKNOWLEDGED"); return repo.save(p);}
    public void delete(Long id,String u,String role){var p=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Review not found")); if(!("ADMIN".equals(role)||"HR".equals(role)||p.getUserId().equals(u))) throw new IllegalStateException("Access denied."); if(!"DRAFT".equals(p.getStatus())&& !"REJECTED".equals(p.getStatus()) && !"ADMIN".equals(role)&& !"HR".equals(role)) throw new IllegalStateException("Only draft or rejected goals can be deleted."); repo.delete(p);}
    public PerformanceReview get(Long id){return repo.findById(id).orElseThrow(()->new IllegalArgumentException("Review not found"));}
    private PerformanceReview owned(Long id,String u){var p=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Review not found")); if(!p.getUserId().equals(u)) throw new IllegalStateException("Access denied."); return p;}
    private Employee findEmployee(String u){
        return employees.findByRegisterEmployee_UserId(u)
                .or(() -> employees.findByEmployeeProfile_UserId(u))
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }
    private boolean blank(String s){return s==null||s.isBlank();}
}
