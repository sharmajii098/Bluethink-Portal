package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.Activity;
import EmployeeManagementSystem.service.ActivityService;
import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityApiController {
    private final ActivityService service;
    private final EmployeeRepository employeeRepository;
    public ActivityApiController(ActivityService service, EmployeeRepository employeeRepository) { this.service = service; this.employeeRepository = employeeRepository; }

    @GetMapping("/visible")
    @PreAuthorize("isAuthenticated()")
    public Map<String,Object> visible(Authentication authentication,
                                      @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size,
                                      @RequestParam(required=false) String userId, @RequestParam(required=false) String module,
                                      @RequestParam(required=false) String action, @RequestParam(required=false) String status,
                                      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) {
        String currentUserId = authentication.getName();
        String role = authentication.getAuthorities().stream().map(a -> a.getAuthority()).filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5)).findFirst().orElse("EMPLOYEE");
        Set<String> allowed = new HashSet<>();
        allowed.add(currentUserId);
        Employee me = employeeRepository.findByRegisterEmployee_UserId(currentUserId).orElse(null);
        if ("ADMIN".equals(role)) {
            employeeRepository.findAll().stream().map(e -> e.getRegisterEmployee() == null ? null : e.getRegisterEmployee().getUserId())
                    .filter(Objects::nonNull).forEach(allowed::add);
        } else if ("HR".equals(role) && me != null && me.getDepartment() != null) {
            employeeRepository.findByDepartmentId(me.getDepartment().getId()).stream().map(e -> e.getRegisterEmployee() == null ? null : e.getRegisterEmployee().getUserId())
                    .filter(Objects::nonNull).forEach(allowed::add);
        } else if ("MANAGER".equals(role) && me != null) {
            employeeRepository.findByReportingManager_Id(me.getId()).stream().map(e -> e.getRegisterEmployee() == null ? null : e.getRegisterEmployee().getUserId())
                    .filter(Objects::nonNull).forEach(allowed::add);
        }
        LocalDateTime fromTime = from == null ? null : from.atStartOfDay();
        LocalDateTime toTime = to == null ? null : to.plusDays(1).atStartOfDay().minusNanos(1);
        Page<Activity> p = service.searchForUsers(allowed, userId, action, module, status, fromTime, toTime, Math.max(page,0), Math.min(Math.max(size,1),100));
        Map<String,Object> out = new LinkedHashMap<>(); out.put("content",p.getContent()); out.put("page",p.getNumber());
        out.put("size",p.getSize()); out.put("totalElements",p.getTotalElements()); out.put("totalPages",p.getTotalPages());
        out.put("hasNext",p.hasNext()); out.put("hasPrevious",p.hasPrevious()); return out;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public Map<String,Object> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size,
                                   @RequestParam(required=false) String userId, @RequestParam(required=false) String module,
                                   @RequestParam(required=false) String action, @RequestParam(required=false) String status,
                                   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                                   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDateTime fromTime = from == null ? null : from.atStartOfDay();
        LocalDateTime toTime = to == null ? null : to.plusDays(1).atStartOfDay().minusNanos(1);
        Page<Activity> p = service.search(userId,module,action,status,fromTime,toTime,Math.max(page,0),Math.min(Math.max(size,1),100));
        Map<String,Object> out = new LinkedHashMap<>(); out.put("content",p.getContent()); out.put("page",p.getNumber());
        out.put("size",p.getSize()); out.put("totalElements",p.getTotalElements()); out.put("totalPages",p.getTotalPages());
        out.put("hasNext",p.hasNext()); out.put("hasPrevious",p.hasPrevious()); return out;
    }
}
