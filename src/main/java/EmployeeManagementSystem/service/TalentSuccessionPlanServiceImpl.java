package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TalentSuccessionPlanServiceImpl implements TalentSuccessionPlanService{
    private static final Set<String> READINESS=Set.of("READY_NOW","SIX_TO_TWELVE_MONTHS","ONE_TO_TWO_YEARS","DEVELOPING");
    private static final Set<String> RISKS=Set.of("LOW","MEDIUM","HIGH");
    private static final Set<String> POTENTIAL=Set.of("HIGH","MEDIUM","CORE");
    private static final Set<String> STATUSES=Set.of("ACTIVE","ON_HOLD","PROMOTED","ARCHIVED");
    private final TalentSuccessionPlanRepository repo; private final EmployeeRepository employees; private final RegisterEmployeeRepository registerEmployees;
    public TalentSuccessionPlanServiceImpl(TalentSuccessionPlanRepository r,EmployeeRepository e,RegisterEmployeeRepository re){repo=r;employees=e;registerEmployees=re;}
    public List<TalentSuccessionPlan> all(){
        List<TalentSuccessionPlan> plans=repo.findAllByOrderByCreatedAtDesc();
        plans.forEach(this::enrichDisplayNames);
        return plans;
    }
    public List<TalentSuccessionPlan> mine(String employeeUserId){
        List<TalentSuccessionPlan> plans=repo.findByEmployeeUserIdOrderByCreatedAtDesc(employeeUserId);
        plans.forEach(this::enrichDisplayNames);
        return plans;
    }
    private void validate(TalentSuccessionPlan p){
        if(p.getEmployeeUserId()==null||p.getEmployeeUserId().isBlank())throw new IllegalArgumentException("Employee user ID is required.");
        if(p.getTargetRole()==null||p.getTargetRole().isBlank())throw new IllegalArgumentException("Target successor role is required.");
        if(p.getReadinessLevel()==null||!READINESS.contains(p.getReadinessLevel().toUpperCase()))throw new IllegalArgumentException("Invalid readiness level.");
        p.setReadinessLevel(p.getReadinessLevel().toUpperCase());
        if(p.getRetentionRisk()!=null&&!p.getRetentionRisk().isBlank()){p.setRetentionRisk(p.getRetentionRisk().toUpperCase());if(!RISKS.contains(p.getRetentionRisk()))throw new IllegalArgumentException("Invalid retention risk.");}
        if(p.getPerformancePotential()!=null&&!p.getPerformancePotential().isBlank()){p.setPerformancePotential(p.getPerformancePotential().toUpperCase());if(!POTENTIAL.contains(p.getPerformancePotential()))throw new IllegalArgumentException("Invalid performance potential.");}
    }
    private void enrich(TalentSuccessionPlan p){
        employees.findByRegisterEmployee_UserId(p.getEmployeeUserId())
            .or(() -> employees.findByEmployeeProfile_UserId(p.getEmployeeUserId()))
            .ifPresent(e->{
                String n=e.getFullName();
                if(n==null||n.isBlank()) n=((e.getFirstName()==null?"":e.getFirstName())+" "+(e.getLastName()==null?"":e.getLastName())).trim();
                p.setEmployeeName(n);
                if(e.getDepartment()!=null) p.setCurrentDepartment(e.getDepartment().getDepartmentName());
            });
    }

    private String displayName(String userId){
        if(userId==null||userId.isBlank()) return null;

        // First resolve the normal Employee record. This is important because
        // manager/mentor users may exist in employees/employee_profile even
        // when register_employee is not linked for older records.
        Optional<Employee> employee = employees.findByRegisterEmployee_UserId(userId)
            .or(() -> employees.findByEmployeeProfile_UserId(userId));
        if(employee.isPresent()){
            Employee e = employee.get();
            String name = e.getFullName();
            if(name==null || name.isBlank()){
                name=((e.getFirstName()==null?"":e.getFirstName())+" "+
                      (e.getLastName()==null?"":e.getLastName())).trim();
            }
            if(!name.isBlank()) return name;
        }

        // Fallback for users that only exist in register_employee.
        RegisterEmployee r=registerEmployees.findByUserId(userId);
        if(r!=null && r.getName()!=null && !r.getName().isBlank()) return r.getName();
        return userId;
    }

    private void enrichDisplayNames(TalentSuccessionPlan p){
        enrich(p);
        p.setMentorName(displayName(p.getMentorUserId()));
        p.setNominatedByName(displayName(p.getNominatedByUserId()));
    }
    public TalentSuccessionPlan create(TalentSuccessionPlan p,String actor){validate(p);if(repo.existsByEmployeeUserIdAndTargetRoleIgnoreCaseAndStatusIn(p.getEmployeeUserId(),p.getTargetRole(),List.of("ACTIVE","ON_HOLD")))throw new IllegalArgumentException("An active succession plan already exists for this employee and target role.");enrich(p);p.setId(null);p.setPlanNumber("TSP-"+System.currentTimeMillis());p.setNominatedByUserId(actor);p.setNominatedByName(displayName(actor));p.setStatus("ACTIVE");TalentSuccessionPlan saved=repo.save(p);enrichDisplayNames(saved);return saved;}
    public TalentSuccessionPlan update(Long id,TalentSuccessionPlan in,String actor){TalentSuccessionPlan p=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Succession plan not found."));if(in.getEmployeeUserId()!=null)p.setEmployeeUserId(in.getEmployeeUserId());if(in.getCurrentDesignation()!=null)p.setCurrentDesignation(in.getCurrentDesignation());if(in.getTargetRole()!=null)p.setTargetRole(in.getTargetRole());if(in.getCriticalRole()!=null)p.setCriticalRole(in.getCriticalRole());if(in.getReadinessLevel()!=null)p.setReadinessLevel(in.getReadinessLevel());if(in.getRetentionRisk()!=null)p.setRetentionRisk(in.getRetentionRisk());if(in.getPerformancePotential()!=null)p.setPerformancePotential(in.getPerformancePotential());if(in.getDevelopmentActions()!=null)p.setDevelopmentActions(in.getDevelopmentActions());if(in.getMentorUserId()!=null)p.setMentorUserId(in.getMentorUserId());validate(p);enrich(p);TalentSuccessionPlan saved=repo.save(p);enrichDisplayNames(saved);return saved;}
    public TalentSuccessionPlan review(Long id,String status,String comment,String actor){TalentSuccessionPlan p=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Succession plan not found."));String s=status==null?"":status.toUpperCase();if(!STATUSES.contains(s))throw new IllegalArgumentException("Invalid plan status.");p.setStatus(s);p.setReviewerUserId(actor);p.setReviewerComment(comment);p.setReviewedAt(LocalDateTime.now());TalentSuccessionPlan saved=repo.save(p);enrichDisplayNames(saved);return saved;}
    public void delete(Long id){if(!repo.existsById(id))throw new IllegalArgumentException("Succession plan not found.");repo.deleteById(id);}
}
