package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.enums.ProjectStatus;
import EmployeeManagementSystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentorships")
public class MentorshipController {

    private final MentorshipAssignmentRepository mentorshipRepo;
    private final EmployeeRepository employeeRepo;
    private final ProjectRepository projectRepo;
    private final RegisterEmployeeRepository registerRepo;

    public MentorshipController(MentorshipAssignmentRepository mentorshipRepo,
                                EmployeeRepository employeeRepo,
                                ProjectRepository projectRepo,
                                RegisterEmployeeRepository registerRepo) {
        this.mentorshipRepo = mentorshipRepo;
        this.employeeRepo = employeeRepo;
        this.projectRepo = projectRepo;
        this.registerRepo = registerRepo;
    }

    @GetMapping
    @Transactional
    public List<Map<String, Object>> list(Authentication auth) {
        String role = role(auth);
        Employee current = ("ADMIN".equals(role) || "HR".equals(role)) ? null : currentEmployee(auth);
        List<MentorshipAssignment> rows;
        if ("ADMIN".equals(role) || "HR".equals(role)) {
            rows = mentorshipRepo.findAll();
        } else if ("MANAGER".equals(role)) {
            Map<Long, MentorshipAssignment> unique = new LinkedHashMap<>();
            mentorshipRepo.findByManager_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
            mentorshipRepo.findByMentor_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
            mentorshipRepo.findByMentee_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
            rows = new ArrayList<>(unique.values());
        } else {
            Map<Long, MentorshipAssignment> unique = new LinkedHashMap<>();
            mentorshipRepo.findByMentee_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
            mentorshipRepo.findByMentor_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
            rows = new ArrayList<>(unique.values());
        }
        return rows.stream()
                .sorted(Comparator.comparing(MentorshipAssignment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::map)
                .toList();
    }

    @GetMapping("/my")
    @Transactional
    public List<Map<String, Object>> my(Authentication auth) {
        Employee current = currentEmployee(auth);
        Map<Long, MentorshipAssignment> unique = new LinkedHashMap<>();
        mentorshipRepo.findByMentee_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
        mentorshipRepo.findByMentor_IdOrderByCreatedAtDesc(current.getId()).forEach(x -> unique.put(x.getId(), x));
        return unique.values().stream().map(this::map).toList();
    }

    /**
     * Employees/projects that the current HR/Manager may use when building a
     * learning hierarchy. A Manager is deliberately restricted to direct
     * reports in the Manager's own department.
     */
    @GetMapping("/eligible")
    @Transactional
    public Map<String, Object> eligible(Authentication auth) {
        String role = role(auth);
        if (!("HR".equals(role) || "MANAGER".equals(role) || "ADMIN".equals(role))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR or Managers can create learning assignments");
        }

        List<Employee> employees;
        List<Project> projects;
        if ("MANAGER".equals(role)) {
            Employee manager = currentEmployee(auth);
            Long departmentId = manager.getDepartment() == null ? null : manager.getDepartment().getId();
            employees = employeeRepo.findByReportingManager_Id(manager.getId()).stream()
                    .filter(e -> departmentId == null || (e.getDepartment() != null && Objects.equals(departmentId, e.getDepartment().getId())))
                    .toList();
            Set<Long> employeeIds = employees.stream().map(Employee::getId).collect(Collectors.toSet());
            projects = projectRepo.findAll().stream()
                    .filter(p -> p.getStatus() == ProjectStatus.ACTIVE || p.getStatus() == ProjectStatus.IN_PROGRESS)
                    .filter(p -> Objects.equals(p.getProjectManagerId(), manager.getId()) ||
                            (p.getAssignedEmployees() != null && p.getAssignedEmployees().stream().anyMatch(e -> employeeIds.contains(e.getId()))))
                    .toList();
        } else {
            employees = employeeRepo.findAll();
            projects = projectRepo.findAll().stream()
                    .filter(p -> p.getStatus() == ProjectStatus.ACTIVE || p.getStatus() == ProjectStatus.IN_PROGRESS)
                    .toList();
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("employees", employees.stream().map(this::employeeMap).toList());
        out.put("projects", projects.stream().map(this::projectMap).toList());
        return out;
    }

    @PostMapping
    @Transactional
    public Map<String, Object> create(Authentication auth, @RequestBody Map<String, Object> body) {
        String role = role(auth);
        if (!("HR".equals(role) || "MANAGER".equals(role))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HR or Managers can create learning/shadow assignments");
        }

        Long mentorId = longValue(body.get("mentorId"));
        Long menteeId = longValue(body.get("menteeId"));
        if (mentorId == null || menteeId == null) bad("Senior mentor and learner are required");
        if (Objects.equals(mentorId, menteeId)) bad("The mentor and learner must be different employees");

        Employee mentor = employeeRepo.findById(mentorId).orElseThrow(() -> badEx("Senior mentor not found"));
        Employee mentee = employeeRepo.findById(menteeId).orElseThrow(() -> badEx("Learner/trainee not found"));
        if (!"EMPLOYEE".equals(normalizeRole(mentor.getRegisterEmployee() == null ? null : mentor.getRegisterEmployee().getRole()))
                || !"EMPLOYEE".equals(normalizeRole(mentee.getRegisterEmployee() == null ? null : mentee.getRegisterEmployee().getRole()))) {
            bad("Senior mentor and learner must both be Employee-role users. The Manager remains the reporting/approval owner.");
        }
        Employee manager;

        if (mentor.getDepartment() == null || mentee.getDepartment() == null ||
                !Objects.equals(mentor.getDepartment().getId(), mentee.getDepartment().getId())) {
            bad("Senior mentor and learner must belong to the same department");
        }

        if ("MANAGER".equals(role)) {
            manager = currentEmployee(auth);
            Set<Long> direct = employeeRepo.findByReportingManager_Id(manager.getId()).stream().map(Employee::getId).collect(Collectors.toSet());
            if (!direct.contains(mentor.getId()) || !direct.contains(mentee.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only build a learning hierarchy between your own direct-report employees");
            }
            if (manager.getDepartment() != null && !Objects.equals(manager.getDepartment().getId(), mentee.getDepartment().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Employees must belong to your department");
            }
        } else {
            manager = mentee.getReportingManager();
            if (manager == null) bad("Assign a reporting Manager to the learner before creating a shadow assignment");
            if (mentor.getReportingManager() == null || !Objects.equals(mentor.getReportingManager().getId(), manager.getId())) {
                bad("Senior mentor and learner must currently report to the same Manager");
            }
        }

        LocalDate startDate = parseDate(body.get("startDate"), LocalDate.now());
        LocalDate endDate = parseOptionalDate(body.get("endDate"));
        if (endDate != null && endDate.isBefore(startDate)) bad("End date cannot be before start date");

        Project project = null;
        Long projectId = longValue(body.get("projectId"));
        if (projectId != null) {
            project = projectRepo.findById(projectId).orElseThrow(() -> badEx("Project not found"));
            if (!(project.getStatus() == ProjectStatus.ACTIVE || project.getStatus() == ProjectStatus.IN_PROGRESS)) {
                bad("Learning can only be linked to an active project");
            }
            boolean mentorOnProject = Objects.equals(project.getProjectManagerId(), mentor.getId()) ||
                    (project.getAssignedEmployees() != null && project.getAssignedEmployees().stream().anyMatch(e -> Objects.equals(e.getId(), mentor.getId())));
            if (!mentorOnProject) bad("The selected senior employee is not assigned to this project");
        }

        final Long selectedProjectId = project == null ? null : project.getId();
        boolean duplicate = mentorshipRepo.findByMentee_IdAndStatusIgnoreCaseOrderByCreatedAtDesc(mentee.getId(), "ACTIVE").stream()
                .anyMatch(a -> Objects.equals(a.getMentor().getId(), mentor.getId()) &&
                        Objects.equals(a.getProject() == null ? null : a.getProject().getId(), selectedProjectId));
        if (duplicate) throw new ResponseStatusException(HttpStatus.CONFLICT, "This active learning/shadow assignment already exists");

        MentorshipAssignment a = new MentorshipAssignment();
        a.setMentor(mentor);
        a.setMentee(mentee);
        a.setManager(manager);
        a.setDepartment(mentee.getDepartment());
        a.setProject(project);
        String type = text(body.get("assignmentType")).toUpperCase(Locale.ENGLISH);
        if (type.isBlank()) type = "SHADOWING";
        if (!List.of("SHADOWING", "MENTORING", "PAIR_LEARNING", "TRAINING").contains(type)) bad("Invalid learning assignment type");
        a.setAssignmentType(type);
        a.setLearningGoal(textOrNull(body.get("learningGoal")));
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setStatus("ACTIVE");
        a.setAssignedByUserId(auth.getName());
        RegisterEmployee account = registerRepo.findByUserId(auth.getName());
        a.setAssignedByName(account == null ? auth.getName() : account.getName());
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedAt(LocalDateTime.now());
        return map(mentorshipRepo.save(a));
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public Map<String, Object> status(Authentication auth, @PathVariable Long id, @RequestBody Map<String, Object> body) {
        MentorshipAssignment a = mentorshipRepo.findById(id).orElseThrow(() -> badEx("Learning assignment not found"));
        String role = role(auth);
        Employee current = "MANAGER".equals(role) ? currentEmployee(auth) : null;
        if (!("HR".equals(role) || "ADMIN".equals(role) ||
                ("MANAGER".equals(role) && current != null && Objects.equals(a.getManager().getId(), current.getId())))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this learning assignment");
        }
        String status = text(body.get("status")).toUpperCase(Locale.ENGLISH);
        if (!List.of("ACTIVE", "COMPLETED", "CANCELLED").contains(status)) bad("Status must be ACTIVE, COMPLETED or CANCELLED");
        a.setStatus(status);
        a.setUpdatedAt(LocalDateTime.now());
        return map(mentorshipRepo.save(a));
    }

    private Map<String, Object> map(MentorshipAssignment a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("mentorId", a.getMentor() == null ? null : a.getMentor().getId());
        m.put("mentorUserId", userId(a.getMentor()));
        m.put("mentorName", a.getMentor() == null ? null : a.getMentor().getFullName());
        m.put("mentorDesignation", designation(a.getMentor()));
        m.put("menteeId", a.getMentee() == null ? null : a.getMentee().getId());
        m.put("menteeUserId", userId(a.getMentee()));
        m.put("menteeName", a.getMentee() == null ? null : a.getMentee().getFullName());
        m.put("menteeDesignation", designation(a.getMentee()));
        m.put("managerId", a.getManager() == null ? null : a.getManager().getId());
        m.put("managerName", a.getManager() == null ? null : a.getManager().getFullName());
        m.put("departmentId", a.getDepartment() == null ? null : a.getDepartment().getId());
        m.put("departmentName", a.getDepartment() == null ? null : a.getDepartment().getDepartmentName());
        m.put("projectId", a.getProject() == null ? null : a.getProject().getId());
        m.put("projectName", a.getProject() == null ? null : a.getProject().getProjectName());
        m.put("assignmentType", a.getAssignmentType());
        m.put("learningGoal", a.getLearningGoal());
        m.put("startDate", a.getStartDate());
        m.put("endDate", a.getEndDate());
        m.put("status", a.getStatus());
        m.put("assignedByUserId", a.getAssignedByUserId());
        m.put("assignedByName", a.getAssignedByName());
        m.put("createdAt", a.getCreatedAt());
        return m;
    }

    private Map<String, Object> employeeMap(Employee e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("userId", userId(e));
        m.put("fullName", e.getFullName());
        m.put("designation", designation(e));
        m.put("role", e.getRegisterEmployee() == null ? null : normalizeRole(e.getRegisterEmployee().getRole()));
        m.put("departmentId", e.getDepartment() == null ? null : e.getDepartment().getId());
        m.put("departmentName", e.getDepartment() == null ? null : e.getDepartment().getDepartmentName());
        m.put("reportingManagerId", e.getReportingManager() == null ? null : e.getReportingManager().getId());
        m.put("reportingManagerName", e.getReportingManager() == null ? null : e.getReportingManager().getFullName());
        return m;
    }

    private Map<String, Object> projectMap(Project p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("projectName", p.getProjectName());
        m.put("projectManagerId", p.getProjectManagerId());
        m.put("projectManagerName", p.getProjectManagerName());
        m.put("status", p.getStatus());
        m.put("assignedEmployeeIds", p.getAssignedEmployees() == null ? List.of() : p.getAssignedEmployees().stream().map(Employee::getId).toList());
        return m;
    }

    private Employee currentEmployee(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        return employeeRepo.findByRegisterEmployee_UserId(auth.getName()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee account is not linked to this user"));
    }

    private String role(Authentication auth) {
        if (auth == null) return "";
        return auth.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_", "").toUpperCase(Locale.ENGLISH)).orElse("");
    }

    private String normalizeRole(String role) {
        String r = role == null ? "" : role.trim().toUpperCase(Locale.ENGLISH);
        return r.startsWith("ROLE_") ? r.substring(5) : r;
    }

    private String userId(Employee e) {
        return e == null || e.getRegisterEmployee() == null ? null : e.getRegisterEmployee().getUserId();
    }

    private String designation(Employee e) {
        return e == null || e.getRegisterEmployee() == null ? null : e.getRegisterEmployee().getDesignation();
    }

    private Long longValue(Object v) {
        if (v == null || String.valueOf(v).isBlank()) return null;
        try { return Long.valueOf(String.valueOf(v)); }
        catch (Exception ex) { throw badEx("Invalid numeric value"); }
    }

    private LocalDate parseDate(Object v, LocalDate fallback) {
        if (v == null || String.valueOf(v).isBlank()) return fallback;
        try { return LocalDate.parse(String.valueOf(v)); }
        catch (Exception ex) { throw badEx("Invalid date"); }
    }

    private LocalDate parseOptionalDate(Object v) {
        if (v == null || String.valueOf(v).isBlank()) return null;
        return parseDate(v, null);
    }

    private String text(Object v) { return v == null ? "" : String.valueOf(v).trim(); }
    private String textOrNull(Object v) { String s = text(v); return s.isBlank() ? null : s; }
    private void bad(String message) { throw badEx(message); }
    private ResponseStatusException badEx(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
