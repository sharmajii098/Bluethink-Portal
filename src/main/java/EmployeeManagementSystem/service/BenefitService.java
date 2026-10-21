package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*; import java.util.*;
public interface BenefitService {
 List<BenefitPlan> plans(boolean all); BenefitPlan savePlan(BenefitPlan p); void deletePlan(Long id);
 List<BenefitEnrollment> myEnrollments(String userId); List<BenefitEnrollment> allEnrollments();
 BenefitEnrollment enroll(Long planId, BenefitEnrollment e, String userId, String name); BenefitEnrollment update(Long id, BenefitEnrollment e, String userId);
 BenefitEnrollment review(Long id,String status,String comment,String reviewer); void cancel(Long id,String userId);
}
