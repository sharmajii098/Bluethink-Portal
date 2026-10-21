package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.LoginLocation;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.service.LoginLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login-locations")
@RequiredArgsConstructor
public class LoginLocationApiController {
    private final LoginLocationService locationService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/visible")
    public ResponseEntity<?> visible(Authentication authentication) {
        String userId = authentication.getName();
        String role = authentication == null ? "EMPLOYEE" : authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(Objects::nonNull)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .map(String::trim)
                .map(value -> value.toUpperCase(Locale.ROOT))
                .filter(value -> Set.of("ADMIN", "HR", "MANAGER", "EMPLOYEE").contains(value))
                .findFirst().orElse("EMPLOYEE");

        Set<String> allowed = new HashSet<>();
        if ("ADMIN".equals(role) || "HR".equals(role)) {
            // Organization-wide scope: do not apply employee/team filtering to HR/Admin.
            List<LoginLocation> all = new ArrayList<>(locationService.getAllLoginLocations());
            all.sort(Comparator.comparing(LoginLocation::getLoginTime,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            return ResponseEntity.ok(all);
        }

        Employee me = employeeRepository.findByRegisterEmployee_UserId(userId).orElse(null);
        if (me == null) return ResponseEntity.status(404).body(Map.of("message", "Employee profile not found."));
        allowed.add(userId);

        if ("MANAGER".equals(role)) {
            employeeRepository.findByReportingManager_Id(me.getId()).stream()
                    .map(e -> e.getRegisterEmployee() == null ? null : e.getRegisterEmployee().getUserId())
                    .filter(Objects::nonNull).forEach(allowed::add);
        }

        List<LoginLocation> result = allowed.stream()
                .flatMap(id -> locationService.getByEmployeeId(id).stream())
                .sorted(Comparator.comparing(LoginLocation::getLoginTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
