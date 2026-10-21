package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.EmployeePrivacyConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface EmployeePrivacyConsentRepository extends JpaRepository<EmployeePrivacyConsent, Long> {
    List<EmployeePrivacyConsent> findByUserIdOrderByCreatedAtDesc(String userId);
    List<EmployeePrivacyConsent> findAllByOrderByCreatedAtDesc();
    boolean existsByUserIdAndConsentTypeAndPolicyVersionAndStatusIn(String userId, String consentType, String policyVersion, Collection<String> statuses);
}
