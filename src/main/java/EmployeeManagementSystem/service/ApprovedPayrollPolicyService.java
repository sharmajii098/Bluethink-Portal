package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.ApprovedPayrollPolicy;
import EmployeeManagementSystem.repository.ApprovedPayrollPolicyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

/** Versioned company-approved payroll policies. Never trust a free-text reference in a payroll request. */
@Service
public class ApprovedPayrollPolicyService {
 private final ApprovedPayrollPolicyRepository records;
 private final PortalActor actor;
 private final ReleaseAudit audit;
 private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();
 public ApprovedPayrollPolicyService(ApprovedPayrollPolicyRepository records,PortalActor actor,ReleaseAudit audit){this.records=records;this.actor=actor;this.audit=audit;}
 public record Create(String effectiveFrom,String effectiveThrough,PayrollReconciliationRules.Policy policy){}
 public record View(Long id,String reference,String effectiveFrom,String effectiveThrough,String status,String createdBy,String approvedBy,PayrollReconciliationRules.Policy policy){}
 private static YearMonth month(String value){
  if(value==null || !value.matches("\\d{4}-(0[1-9]|1[0-2])"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use YYYY-MM for policy effective dates");
  YearMonth month=YearMonth.parse(value);
  if(month.getYear()<2000||month.getYear()>2100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Policy effective year is outside allowed range");
  return month;
 }
 private String write(PayrollReconciliationRules.Policy policy){try{return json.writeValueAsString(policy);}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Policy serialization failed");}}
 private PayrollReconciliationRules.Policy read(String value){try{return json.readValue(value,PayrollReconciliationRules.Policy.class);}catch(Exception e){throw new IllegalStateException("Saved payroll policy is invalid",e);}}
 private View view(ApprovedPayrollPolicy row){return new View(row.id,row.reference,row.effectiveFrom,row.effectiveThrough,row.status,row.createdBy,row.approvedBy,read(row.policyJson));}
 @Transactional(readOnly=true) public List<View> list(){actor.hr();return records.findAllByOrderByCreatedAtDesc().stream().map(this::view).toList();}
 @Transactional public View create(Create input){
  actor.hr();if(input==null||input.policy()==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Company payroll policy is required");
  String from=month(input.effectiveFrom()).toString();String through=input.effectiveThrough()==null||input.effectiveThrough().isBlank()?null:month(input.effectiveThrough()).toString();
  if(through!=null&&through.compareTo(from)<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Policy end precedes start");
  PayrollReconciliationRules.Policy p=input.policy();
  if(!p.reference().equals(p.reference().trim()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Policy version reference must not include leading or trailing whitespace");
  if(records.findByReference(p.reference()).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Policy reference already exists. Use a new version reference");
  ApprovedPayrollPolicy row=new ApprovedPayrollPolicy();row.reference=p.reference().trim();row.policyJson=write(p);row.effectiveFrom=from;row.effectiveThrough=through;
  row.createdBy=actor.id();row.createdAt=OffsetDateTime.now();row.status="DRAFT";
  ApprovedPayrollPolicy saved=records.save(row);audit.record("PAYROLL_POLICY","CREATE",saved.id);return view(saved);
 }
 @Transactional public View approve(Long id){
  actor.hr();ApprovedPayrollPolicy row=records.lock(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Payroll policy not found"));
  if(!"DRAFT".equals(row.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only drafts can be approved");
  if(actor.id().equals(row.createdBy))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"A different HR/Admin must approve company policy");
  if(records.findAllByOrderByCreatedAtDesc().stream().anyMatch(p->!Objects.equals(p.id,row.id)&&"APPROVED".equals(p.status)&&p.effectiveFrom.equals(row.effectiveFrom)))throw new ResponseStatusException(HttpStatus.CONFLICT,"An approved policy already starts this month. Use a different effective month or retain the existing rule");
  read(row.policyJson);row.status="APPROVED";row.approvedBy=actor.id();row.approvedAt=OffsetDateTime.now();audit.record("PAYROLL_POLICY","APPROVE",row.id);return view(records.save(row));
 }
 /** Executed again during payroll approval and generation. If no approved matching rule exists, fail closed. */
 @Transactional(readOnly=true) public void requireApproved(PayrollReconciliationRules.Policy submitted,String period){
  actor.hr();if(submitted==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Select an approved payroll policy");
  ApprovedPayrollPolicy p=records.findByReference(submitted.reference().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.CONFLICT,"Payroll policy has not been registered and approved"));
  if(!"APPROVED".equals(p.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Policy is awaiting independent HR approval");
  YearMonth month=month(period);
  if(month.isBefore(month(p.effectiveFrom))||(p.effectiveThrough!=null&&month.isAfter(month(p.effectiveThrough))))throw new ResponseStatusException(HttpStatus.CONFLICT,"Approved policy is not effective for the selected payroll month");
  // Later approved versions supersede earlier versions without mutating approved historical snapshots.
  List<ApprovedPayrollPolicy> effective=records.findAllByOrderByCreatedAtDesc().stream()
   .filter(other -> "APPROVED".equals(other.status)&&!month(other.effectiveFrom).isAfter(month)&&
      (other.effectiveThrough==null||!month(other.effectiveThrough).isBefore(month))).toList();
  String latest=effective.stream().map(other->other.effectiveFrom).max(String::compareTo).orElse("");
  long winners=effective.stream().filter(other->other.effectiveFrom.equals(latest)).count();
  if(winners!=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Conflicting approved payroll policy versions require HR reconciliation");
  if(!p.effectiveFrom.equals(latest))throw new ResponseStatusException(HttpStatus.CONFLICT,"A newer approved payroll policy applies to this month. Select the latest effective version");
  if(!read(p.policyJson).equals(submitted))throw new ResponseStatusException(HttpStatus.CONFLICT,"Policy values differ from the approved version. Select the saved policy instead of editing its rules");
 }
}
