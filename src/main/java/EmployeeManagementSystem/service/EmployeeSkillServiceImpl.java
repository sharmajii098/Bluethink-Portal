package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeSkill;
import EmployeeManagementSystem.repository.EmployeeSkillRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class EmployeeSkillServiceImpl implements EmployeeSkillService{
 private final EmployeeSkillRepository repo; public EmployeeSkillServiceImpl(EmployeeSkillRepository r){repo=r;}
 public List<EmployeeSkill> my(String u){return repo.findByUserIdOrderBySkillNameAsc(u);} public List<EmployeeSkill> all(){return repo.findAllByOrderByCreatedAtDesc();}
 private void validate(EmployeeSkill x){if(x.getSkillName()==null||x.getSkillName().isBlank())throw new IllegalArgumentException("Skill name is required."); if(x.getProficiency()==null||!Set.of("BEGINNER","INTERMEDIATE","ADVANCED","EXPERT").contains(x.getProficiency().toUpperCase()))throw new IllegalArgumentException("Invalid proficiency."); if(x.getYearsOfExperience()!=null&&(x.getYearsOfExperience()<0||x.getYearsOfExperience()>60))throw new IllegalArgumentException("Years of experience must be between 0 and 60.");}
 public EmployeeSkill create(EmployeeSkill x,String u,String n){validate(x);if(repo.existsByUserIdAndSkillNameIgnoreCaseAndStatusIn(u,x.getSkillName(),List.of("PENDING","UNDER_REVIEW","VERIFIED")))throw new IllegalArgumentException("An active record for this skill already exists.");x.setId(null);x.setRecordNumber("SKL-"+System.currentTimeMillis());x.setUserId(u);x.setEmployeeName(n);x.setStatus("PENDING");return repo.save(x);}
 public EmployeeSkill update(Long id,EmployeeSkill x,String u,boolean mgmt){EmployeeSkill e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Skill record not found."));if(!mgmt&&!u.equals(e.getUserId()))throw new SecurityException("Access denied.");validate(x);e.setSkillName(x.getSkillName());e.setCategory(x.getCategory());e.setProficiency(x.getProficiency().toUpperCase());e.setYearsOfExperience(x.getYearsOfExperience());e.setLastUsed(x.getLastUsed());e.setCertificationName(x.getCertificationName());e.setCertificationReference(x.getCertificationReference());e.setNotes(x.getNotes());if(!mgmt){e.setStatus("PENDING");e.setReviewerUserId(null);e.setReviewerComment(null);e.setReviewedAt(null);}return repo.save(e);}
 public void delete(Long id,String u,boolean mgmt){EmployeeSkill e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Skill record not found."));if(!mgmt&&!u.equals(e.getUserId()))throw new SecurityException("Access denied.");repo.delete(e);}
 public EmployeeSkill review(Long id,String u,String status,String comment){EmployeeSkill e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Skill record not found."));if(!Set.of("UNDER_REVIEW","VERIFIED","REJECTED").contains(status))throw new IllegalArgumentException("Invalid review status.");e.setStatus(status);e.setReviewerUserId(u);e.setReviewerComment(comment);e.setReviewedAt(LocalDateTime.now());return repo.save(e);}
}
