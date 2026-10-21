package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*; import EmployeeManagementSystem.repository.*; import org.springframework.stereotype.Service; import java.time.LocalDate; import java.util.*;
@Service public class RecruitmentServiceImpl implements RecruitmentService {
 private final JobOpeningRepository jobs; private final CandidateRepository candidates; private final OnboardingTaskRepository tasks;
 public RecruitmentServiceImpl(JobOpeningRepository j,CandidateRepository c,OnboardingTaskRepository t){jobs=j;candidates=c;tasks=t;}
 public List<JobOpening> openings(){return jobs.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,"createdAt"));}
 public List<JobOpening> openOpenings(){return jobs.findByStatusOrderByCreatedAtDesc("OPEN");}
 public List<JobOpening> openOpeningsForDepartment(String department){if(department==null||department.isBlank()) return openOpenings(); return jobs.findByStatusAndDepartmentIgnoreCaseOrderByCreatedAtDesc("OPEN",department);}
 public JobOpening saveOpening(JobOpening j){return jobs.save(j);} public void deleteOpening(Long id){jobs.deleteById(id);}
 public List<Candidate> candidates(){return candidates.findAllByOrderByUpdatedAtDesc();} public List<Candidate> candidatesByJob(Long id){return candidates.findByJobOpening_IdOrderByUpdatedAtDesc(id);}
 public List<Candidate> candidatesByDepartment(String department){if(department==null||department.isBlank()) return List.of(); return candidates.findByJobOpening_DepartmentIgnoreCaseOrderByUpdatedAtDesc(department);}
 public Candidate saveCandidate(Candidate c){if(c.getJobOpening()==null) throw new IllegalArgumentException("Job opening is required"); return candidates.save(c);} public void deleteCandidate(Long id){candidates.deleteById(id);}
 public List<OnboardingTask> onboarding(Long employeeId){return tasks.findByEmployeeIdOrderByDueDateAscIdAsc(employeeId);} public List<OnboardingTask> allOnboarding(){return tasks.findAllByOrderByDueDateAscIdAsc();} public List<OnboardingTask> teamOnboarding(List<Long> employeeIds){if(employeeIds==null||employeeIds.isEmpty()) return List.of(); return tasks.findByEmployeeIdInOrderByDueDateAscIdAsc(employeeIds);} public OnboardingTask saveTask(OnboardingTask t){if(t.getEmployeeId()==null||t.getTitle()==null||t.getTitle().isBlank())throw new IllegalArgumentException("Employee and task title are required"); return tasks.save(t);} public void deleteTask(Long id){tasks.deleteById(id);}
 public OnboardingTask completeTask(Long id,Long employeeId){OnboardingTask t=tasks.findById(id).orElseThrow(); if(!Objects.equals(t.getEmployeeId(),employeeId))throw new SecurityException("Task does not belong to employee"); t.setStatus("COMPLETED"); t.setCompletedDate(LocalDate.now()); return tasks.save(t);}
}
