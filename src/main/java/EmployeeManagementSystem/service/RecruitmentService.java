package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*; import java.util.*;
public interface RecruitmentService {
 List<JobOpening> openings();
 List<JobOpening> openOpenings();
 List<JobOpening> openOpeningsForDepartment(String department);
 JobOpening saveOpening(JobOpening j); void deleteOpening(Long id);
 List<Candidate> candidates();
 List<Candidate> candidatesByJob(Long id);
 List<Candidate> candidatesByDepartment(String department);
 Candidate saveCandidate(Candidate c); void deleteCandidate(Long id);
 List<OnboardingTask> onboarding(Long employeeId); List<OnboardingTask> allOnboarding(); List<OnboardingTask> teamOnboarding(List<Long> employeeIds);
 OnboardingTask saveTask(OnboardingTask t); void deleteTask(Long id); OnboardingTask completeTask(Long id,Long employeeId);
}
