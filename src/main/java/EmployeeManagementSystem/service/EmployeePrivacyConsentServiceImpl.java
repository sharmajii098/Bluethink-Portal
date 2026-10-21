package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeePrivacyConsent;
import EmployeeManagementSystem.repository.EmployeePrivacyConsentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmployeePrivacyConsentServiceImpl implements EmployeePrivacyConsentService {
    private final EmployeePrivacyConsentRepository repo;
    public EmployeePrivacyConsentServiceImpl(EmployeePrivacyConsentRepository repo){this.repo=repo;}
    public List<EmployeePrivacyConsent> my(String userId){return repo.findByUserIdOrderByCreatedAtDesc(userId);}
    public List<EmployeePrivacyConsent> all(){return repo.findAllByOrderByCreatedAtDesc();}
    public EmployeePrivacyConsent create(EmployeePrivacyConsent x,String userId,String employeeName){
        if(x.getConsentType()==null||x.getConsentType().isBlank()) throw new IllegalArgumentException("Consent type is required.");
        if(x.getTitle()==null||x.getTitle().isBlank()) throw new IllegalArgumentException("Consent title is required.");
        if(x.getPolicyVersion()==null||x.getPolicyVersion().isBlank()) throw new IllegalArgumentException("Policy version is required.");
        if(repo.existsByUserIdAndConsentTypeAndPolicyVersionAndStatusIn(userId,x.getConsentType(),x.getPolicyVersion(),Set.of("PENDING","GRANTED")))
            throw new IllegalArgumentException("An active consent for this policy version already exists.");
        x.setId(null); x.setConsentNumber("CONS-"+System.currentTimeMillis()); x.setUserId(userId); x.setEmployeeName(employeeName); x.setStatus("PENDING"); x.setConsentedAt(null); x.setWithdrawnAt(null);
        return repo.save(x);
    }
    public EmployeePrivacyConsent updateDecision(Long id,String status,String comment,String reviewerUserId){
        EmployeePrivacyConsent x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Consent not found."));
        if(!Set.of("PENDING","GRANTED","WITHDRAWN","REJECTED").contains(status)) throw new IllegalArgumentException("Invalid consent status.");
        x.setStatus(status); x.setEmployeeComment(comment);
        if("GRANTED".equals(status)) {x.setConsentedAt(LocalDateTime.now()); x.setWithdrawnAt(null);}
        if("WITHDRAWN".equals(status)) x.setWithdrawnAt(LocalDateTime.now());
        return repo.save(x);
    }
}
