package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.repository.JobOpeningRepository;
import EmployeeManagementSystem.service.RecruitmentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api/recruitment")
public class RecruitmentController {
    private final RecruitmentService service;
    private final JwtUtil jwt;
    private final EmployeeRepository employees;
    private final JobOpeningRepository jobs;

    public RecruitmentController(RecruitmentService s, JwtUtil j, EmployeeRepository e, JobOpeningRepository jobs) {
        service = s; jwt = j; employees = e; this.jobs = jobs;
    }

    private String token(String a) {
        if (a == null || !a.startsWith("Bearer ")) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        return a.substring(7);
    }
    private String user(String a) { return jwt.extractUsername(token(a)); }
    private String role(String a) { String r = jwt.extractRole(token(a)); return r == null ? "" : r.replace("ROLE_", "").toUpperCase(); }
    private void mgmt(String r) { if (!"ADMIN".equals(r) && !"HR".equals(r)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Recruitment management access required."); }
    private Employee currentEmployee(String a) {
        String u = user(a);
        return employees.findByRegisterEmployee_UserId(u)
                .or(() -> employees.findByEmployeeProfile_UserId(u))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee profile not found."));
    }
    private void internalUser(String r) {
        if (!"EMPLOYEE".equals(r) && !"MANAGER".equals(r))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Internal job access required.");
    }

    // ADMIN/HR see all published openings; MANAGER sees openings in their department; EMPLOYEE sees all published openings.
    @GetMapping("/openings")
    public List<JobOpening> openings(@RequestHeader("Authorization") String a) {
        String r = role(a);
        if ("MANAGER".equals(r)) {
            Employee e = currentEmployee(a);
            return service.openOpeningsForDepartment(e.getDepartment() == null ? null : e.getDepartment().getDepartmentName());
        }
        return service.openOpenings();
    }

    @GetMapping("/openings/all")
    public List<JobOpening> allOpenings(@RequestHeader("Authorization") String a) { mgmt(role(a)); return service.openings(); }
    @PostMapping("/openings")
    public JobOpening createOpening(@RequestHeader("Authorization") String a, @RequestBody JobOpening j) { mgmt(role(a)); j.setId(null); return service.saveOpening(j); }
    @PutMapping("/openings/{id}")
    public JobOpening updateOpening(@PathVariable Long id, @RequestHeader("Authorization") String a, @RequestBody JobOpening j) { mgmt(role(a)); j.setId(id); return service.saveOpening(j); }
    @DeleteMapping("/openings/{id}")
    public Map<String,Object> deleteOpening(@PathVariable Long id, @RequestHeader("Authorization") String a) { mgmt(role(a)); service.deleteOpening(id); return Map.of("success", true); }

    @GetMapping("/candidates")
    public List<Candidate> candidates(@RequestHeader("Authorization") String a) { mgmt(role(a)); return service.candidates(); }
    @GetMapping("/candidates/relevant")
    public List<Candidate> relevantCandidates(@RequestHeader("Authorization") String a) {
        if (!"MANAGER".equals(role(a))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Manager recruitment access required.");
        Employee e = currentEmployee(a);
        return service.candidatesByDepartment(e.getDepartment() == null ? null : e.getDepartment().getDepartmentName());
    }
    @GetMapping("/candidates/job/{id}")
    public List<Candidate> candidatesByJob(@PathVariable Long id, @RequestHeader("Authorization") String a) { mgmt(role(a)); return service.candidatesByJob(id); }
    @PostMapping("/candidates")
    public Candidate createCandidate(@RequestHeader("Authorization") String a, @RequestBody Candidate c) { mgmt(role(a)); c.setId(null); try { return service.saveCandidate(c); } catch (IllegalArgumentException x) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, x.getMessage()); } }
    @PutMapping("/candidates/{id}")
    public Candidate updateCandidate(@PathVariable Long id, @RequestHeader("Authorization") String a, @RequestBody Candidate c) { mgmt(role(a)); c.setId(id); return service.saveCandidate(c); }
    @DeleteMapping("/candidates/{id}")
    public Map<String,Object> deleteCandidate(@PathVariable Long id, @RequestHeader("Authorization") String a) { mgmt(role(a)); service.deleteCandidate(id); return Map.of("success", true); }

    @PostMapping("/candidates/internal/apply")
    public Candidate apply(@RequestHeader("Authorization") String a, @RequestBody Candidate request) {
        internalUser(role(a));
        Employee e = currentEmployee(a);
        if (request.getJobOpening() == null || request.getJobOpening().getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job opening is required.");
        JobOpening job = jobs.findById(request.getJobOpening().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job opening not found."));
        if (!"OPEN".equalsIgnoreCase(job.getStatus())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This job opening is not accepting applications.");
        Candidate c = new Candidate();
        c.setJobOpening(job); c.setFullName(e.getFullName()); c.setEmail(e.getEmail()); c.setPhone(e.getPhone());
        c.setDob(e.getDateOfBirth()); c.setExperience(request.getExperience()); c.setResumeUrl(request.getResumeUrl());
        c.setNotes(request.getNotes()); c.setStage("APPLIED"); c.setSource("INTERNAL_APPLICATION"); c.setApplicantEmployeeId(e.getId());
        return service.saveCandidate(c);
    }

    @PostMapping("/candidates/internal/refer")
    public Candidate refer(@RequestHeader("Authorization") String a, @RequestBody Candidate request) {
        internalUser(role(a));
        Employee referrer = currentEmployee(a);
        if (request.getJobOpening() == null || request.getJobOpening().getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job opening is required.");
        if (request.getFullName() == null || request.getFullName().isBlank() || request.getEmail() == null || request.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Candidate name and email are required.");
        JobOpening job = jobs.findById(request.getJobOpening().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job opening not found."));
        if (!"OPEN".equalsIgnoreCase(job.getStatus())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This job opening is not accepting referrals.");
        Candidate c = new Candidate();
        c.setJobOpening(job); c.setFullName(request.getFullName()); c.setEmail(request.getEmail()); c.setPhone(request.getPhone());
        c.setDob(request.getDob()); c.setExperience(request.getExperience()); c.setResumeUrl(request.getResumeUrl()); c.setNotes(request.getNotes());
        c.setStage("APPLIED"); c.setSource("EMPLOYEE_REFERRAL"); c.setReferredByEmployeeId(referrer.getId());
        return service.saveCandidate(c);
    }

    @GetMapping("/onboarding/list/my")
    public List<OnboardingTask> myOnboarding(@RequestHeader("Authorization") String a) { return service.onboarding(currentEmployee(a).getId()); }
    @GetMapping("/onboarding/list/manage")
    public List<OnboardingTask> allOnboarding(@RequestHeader("Authorization") String a) { mgmt(role(a)); return service.allOnboarding(); }
    @GetMapping("/onboarding/list/team")
    public List<OnboardingTask> teamOnboarding(@RequestHeader("Authorization") String a) {
        if (!"MANAGER".equals(role(a))) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Manager onboarding access required.");
        Long managerId = currentEmployee(a).getId();
        List<Long> ids = employees.findByReportingManager_Id(managerId).stream().map(Employee::getId).toList();
        return service.teamOnboarding(ids);
    }
    @GetMapping("/onboarding/employee/{employeeId}")
    public List<OnboardingTask> onboarding(@PathVariable Long employeeId, @RequestHeader("Authorization") String a) {
        String r = role(a);
        if (!"ADMIN".equals(r) && !"HR".equals(r)) {
            Employee own = currentEmployee(a);
            if ("MANAGER".equals(r)) {
                boolean direct = employees.findByReportingManager_Id(own.getId()).stream().anyMatch(e -> Objects.equals(e.getId(), employeeId));
                if (!direct) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view onboarding for your direct reports.");
            } else if (!Objects.equals(own.getId(), employeeId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied.");
            }
        }
        return service.onboarding(employeeId);
    }
    @PostMapping("/onboarding")
    public OnboardingTask createTask(@RequestHeader("Authorization") String a, @RequestBody OnboardingTask t) { mgmt(role(a)); t.setId(null); try { return service.saveTask(t); } catch (IllegalArgumentException x) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, x.getMessage()); } }
    @PutMapping("/onboarding/{id}")
    public OnboardingTask updateTask(@PathVariable Long id, @RequestHeader("Authorization") String a, @RequestBody OnboardingTask t) { mgmt(role(a)); t.setId(id); return service.saveTask(t); }
    @PostMapping("/onboarding/{id}/complete")
    public OnboardingTask completeTask(@PathVariable Long id, @RequestHeader("Authorization") String a) { try { return service.completeTask(id, currentEmployee(a).getId()); } catch (SecurityException x) { throw new ResponseStatusException(HttpStatus.FORBIDDEN, x.getMessage()); } }
    @DeleteMapping("/onboarding/{id}")
    public Map<String,Object> deleteTask(@PathVariable Long id, @RequestHeader("Authorization") String a) { mgmt(role(a)); service.deleteTask(id); return Map.of("success", true); }
}
