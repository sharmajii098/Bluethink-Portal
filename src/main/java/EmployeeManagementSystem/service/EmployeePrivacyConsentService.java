package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeePrivacyConsent;
import java.util.List;

public interface EmployeePrivacyConsentService {
    List<EmployeePrivacyConsent> my(String userId);
    List<EmployeePrivacyConsent> all();
    EmployeePrivacyConsent create(EmployeePrivacyConsent request, String userId, String employeeName);
    EmployeePrivacyConsent updateDecision(Long id, String status, String comment, String reviewerUserId);
}
