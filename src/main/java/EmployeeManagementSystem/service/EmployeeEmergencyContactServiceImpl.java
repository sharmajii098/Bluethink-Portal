package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.EmployeeEmergencyContact;
import EmployeeManagementSystem.repository.EmployeeEmergencyContactRepository;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class EmployeeEmergencyContactServiceImpl implements EmployeeEmergencyContactService{
 private final EmployeeEmergencyContactRepository repo; public EmployeeEmergencyContactServiceImpl(EmployeeEmergencyContactRepository r){repo=r;}
 public List<EmployeeEmergencyContact> my(String u){return repo.findByUserIdOrderByPrimaryContactDescCreatedAtAsc(u);}
 public List<EmployeeEmergencyContact> all(){return repo.findAllByOrderByCreatedAtDesc();}
 private void validate(EmployeeEmergencyContact x){if(x.getContactName()==null||x.getContactName().isBlank())throw new IllegalArgumentException("Contact name is required.");if(x.getPhone()==null||x.getPhone().isBlank())throw new IllegalArgumentException("Phone is required.");if(x.getContactType()==null||!Set.of("EMERGENCY","DEPENDENT").contains(x.getContactType()))throw new IllegalArgumentException("Invalid contact type.");}
 public EmployeeEmergencyContact create(EmployeeEmergencyContact x,String u,String n){validate(x);if(repo.existsByUserIdAndContactTypeAndContactNameIgnoreCase(u,x.getContactType(),x.getContactName()))throw new IllegalArgumentException("This contact already exists.");x.setId(null);x.setRecordNumber((x.getContactType().equals("DEPENDENT")?"DEP-":"EMC-")+System.currentTimeMillis());x.setUserId(u);x.setEmployeeName(n);x.setVerified(false);return repo.save(x);}
 public EmployeeEmergencyContact update(Long id,EmployeeEmergencyContact x,String u,boolean mgmt){EmployeeEmergencyContact e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Contact not found."));if(!mgmt&&!u.equals(e.getUserId()))throw new SecurityException("Access denied.");validate(x);e.setContactType(x.getContactType());e.setContactName(x.getContactName());e.setRelationship(x.getRelationship());e.setPhone(x.getPhone());e.setAlternatePhone(x.getAlternatePhone());e.setEmail(x.getEmail());e.setAddress(x.getAddress());e.setPrimaryContact(Boolean.TRUE.equals(x.getPrimaryContact()));e.setDateOfBirth(x.getDateOfBirth());e.setFinanciallyDependent(Boolean.TRUE.equals(x.getFinanciallyDependent()));e.setNotes(x.getNotes());if(!mgmt)e.setVerified(false);return repo.save(e);}
 public void delete(Long id,String u,boolean mgmt){EmployeeEmergencyContact e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Contact not found."));if(!mgmt&&!u.equals(e.getUserId()))throw new SecurityException("Access denied.");repo.delete(e);}
 public EmployeeEmergencyContact verify(Long id,String r){EmployeeEmergencyContact e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Contact not found."));e.setVerified(true);return repo.save(e);}
}
