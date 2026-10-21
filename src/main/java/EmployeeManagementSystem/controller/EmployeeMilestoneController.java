package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.service.EmployeeMilestoneService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-milestones")
public class EmployeeMilestoneController {
    private final EmployeeMilestoneService service;
    private final JwtUtil jwt;

    public EmployeeMilestoneController(EmployeeMilestoneService service, JwtUtil jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    private void requireValidToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.substring(7).isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        try {
            jwt.extractUsername(authorization.substring(7));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired session.");
        }
    }

    @GetMapping("/upcoming")
    public List<Map<String, Object>> upcoming(@RequestHeader("Authorization") String authorization,
                                               @RequestParam(defaultValue = "30") int days) {
        requireValidToken(authorization);
        if (days < 1 || days > 366) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "days must be between 1 and 366.");
        return service.upcoming(days);
    }

    @GetMapping("/month")
    public List<Map<String, Object>> month(@RequestHeader("Authorization") String authorization,
                                            @RequestParam(required = false) Integer year,
                                            @RequestParam(required = false) Integer month) {
        requireValidToken(authorization);
        LocalDate today = LocalDate.now();
        int requestedYear = year == null ? today.getYear() : year;
        int requestedMonth = month == null ? today.getMonthValue() : month;
        if (requestedYear < 1900 || requestedYear > 2200 || requestedMonth < 1 || requestedMonth > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid year and month are required.");
        }
        return service.month(requestedYear, requestedMonth);
    }
}
