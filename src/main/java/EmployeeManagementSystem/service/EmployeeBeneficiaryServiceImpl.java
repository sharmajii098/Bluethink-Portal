package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeeBeneficiary;
import EmployeeManagementSystem.repository.EmployeeBeneficiaryRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

@Service
public class EmployeeBeneficiaryServiceImpl implements EmployeeBeneficiaryService{
 private final EmployeeBeneficiaryRepository repo;
 public EmployeeBeneficiaryServiceImpl(EmployeeBeneficiaryRepository r){repo=r;}
 public List<EmployeeBeneficiary> my(String u){return repo.findByUserIdOrderByBenefitTypeAscAllocationPercentageDescCreatedAtAsc(u);}
 public List<EmployeeBeneficiary> all(){return repo.findAllByOrderByCreatedAtDesc();}
 private void validate(EmployeeBeneficiary x){
  if(x.getBenefitType()==null||!Set.of("GRATUITY","PF","INSURANCE","LIFE_INSURANCE","SUPERANNUATION","OTHER").contains(x.getBenefitType()))throw new IllegalArgumentException("Invalid benefit type.");
  if(x.getNomineeName()==null||x.getNomineeName().isBlank())throw new IllegalArgumentException("Nominee name is required.");
  BigDecimal p=x.getAllocationPercentage(); if(p==null||p.compareTo(BigDecimal.ZERO)<=0||p.compareTo(new BigDecimal("100"))>0)throw new IllegalArgumentException("Allocation percentage must be greater than 0 and at most 100.");
 }
 private void validateTotal(String u,String type,BigDecimal p,Long excludeId){
  BigDecimal total=repo.findByUserIdAndBenefitTypeOrderByCreatedAtAsc(u,type).stream().filter(e->excludeId==null||!e.getId().equals(excludeId)).map(e->e.getAllocationPercentage()==null?BigDecimal.ZERO:e.getAllocationPercentage()).reduce(BigDecimal.ZERO,BigDecimal::add);
  if(total.add(p).compareTo(new BigDecimal("100"))>0)throw new IllegalArgumentException("Total allocation for "+type+" cannot exceed 100%.");
 }
 public EmployeeBeneficiary create(EmployeeBeneficiary x,String u,String n){validate(x);if(repo.existsByUserIdAndBenefitTypeAndNomineeNameIgnoreCase(u,x.getBenefitType(),x.getNomineeName()))throw new IllegalArgumentException("This nominee already exists for the selected benefit.");validateTotal(u,x.getBenefitType(),x.getAllocationPercentage(),null);x.setId(null);x.setRecordNumber("BEN-"+System.currentTimeMillis());x.setUserId(u);x.setEmployeeName(n);x.setStatus("PENDING");return repo.save(x);}
 public EmployeeBeneficiary update(Long id,EmployeeBeneficiary x,String u,boolean mgmt){EmployeeBeneficiary e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Beneficiary not found."));if(!mgmt&&!u.equals(e.getUserId()))throw new SecurityException("Access denied.");validate(x);if(!e.getBenefitType().equals(x.getBenefitType())||!e.getNomineeName().equalsIgnoreCase(x.getNomineeName())){if(repo.existsByUserIdAndBenefitTypeAndNomineeNameIgnoreCase(e.getUserId(),x.getBenefitType(),x.getNomineeName()))throw new IllegalArgumentException("This nominee already exists for the selected benefit.");}validateTotal(e.getUserId(),x.getBenefitType(),x.getAllocationPercentage(),e.getId());e.setBenefitType(x.getBenefitType());e.setNomineeName(x.getNomineeName());e.setRelationship(x.getRelationship());e.setDateOfBirth(x.getDateOfBirth());e.setAllocationPercentage(x.getAllocationPercentage());e.setPhone(x.getPhone());e.setEmail(x.getEmail());e.setAddress(x.getAddress());e.setGuardianName(x.getGuardianName());e.setGuardianRelationship(x.getGuardianRelationship());e.setNotes(x.getNotes());if(!mgmt){e.setStatus("PENDING");e.setReviewerUserId(null);e.setReviewerComment(null);e.setReviewedAt(null);}return repo.save(e);}
 public void delete(Long id,String u,boolean mgmt){EmployeeBeneficiary e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Beneficiary not found."));if(!mgmt&&!u.equals(e.getUserId()))throw new SecurityException("Access denied.");repo.delete(e);}
 public EmployeeBeneficiary review(Long id,String status,String comment,String reviewer){EmployeeBeneficiary e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Beneficiary not found."));if(!Set.of("UNDER_REVIEW","VERIFIED","REJECTED").contains(status))throw new IllegalArgumentException("Invalid review status.");e.setStatus(status);e.setReviewerUserId(reviewer);e.setReviewerComment(comment);e.setReviewedAt(java.time.LocalDateTime.now());return repo.save(e);}
}
