package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.PerformanceReview;
import EmployeeManagementSystem.security.jwt.JwtUtil;
import EmployeeManagementSystem.service.PerformanceReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
public class PerformanceReviewController {
    private final PerformanceReviewService service;
    private final JwtUtil jwt;

    public PerformanceReviewController(PerformanceReviewService service, JwtUtil jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    private String token(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        return authorization.substring(7);
    }

    private String user(String authorization) {
        return jwt.extractUsername(token(authorization));
    }

    private String role(String authorization) {
        String value = jwt.extractRole(token(authorization));
        return value == null ? null : value.replace("ROLE_", "").toUpperCase();
    }

    private RuntimeException bad(Exception e) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                e.getMessage() == null ? "Performance request failed." : e.getMessage(),
                e
        );
    }

    /**
     * Always return a plain JSON array of lightweight review objects.
     * Returning the JPA PerformanceReview entity directly exposes the Employee
     * relationship and can produce a large/nested JSON graph. The React page
     * only needs the fields below, so keep the API contract explicit.
     */
    @GetMapping
    public List<Map<String, Object>> list(@RequestHeader("Authorization") String authorization) {
        try {
            return toMaps(service.list(user(authorization), role(authorization)));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    /** Explicit employee endpoint for clients that want an unambiguous self view. */
    @GetMapping("/my")
    public List<Map<String, Object>> my(@RequestHeader("Authorization") String authorization) {
        try {
            return toMaps(service.list(user(authorization), "EMPLOYEE"));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    /** Explicit manager endpoint for team performance records. */
    @GetMapping("/team")
    public List<Map<String, Object>> team(@RequestHeader("Authorization") String authorization) {
        try {
            String currentRole = role(authorization);
            if (!"MANAGER".equals(currentRole)) {
                throw new IllegalStateException("Only managers can view team performance.");
            }
            return toMaps(service.list(user(authorization), "MANAGER"));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    @PostMapping
    public Map<String, Object> create(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body
    ) {
        try {
            return toMap(service.create(user(authorization), body.get("cycle"), body.get("goal"), body.get("dueDate")));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body
    ) {
        try {
            return toMap(service.updateSelf(
                    id,
                    user(authorization),
                    body.get("selfComment"),
                    body.get("goal"),
                    body.get("dueDate")
            ));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    @PostMapping("/{id}/submit")
    public Map<String, Object> submit(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            return toMap(service.submit(id, user(authorization)));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    @PostMapping("/{id}/review")
    public Map<String, Object> review(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> body
    ) {
        try {
            Integer rating = body.get("rating") == null
                    ? null
                    : Integer.valueOf(body.get("rating").toString());
            return toMap(service.review(id, user(authorization), rating, (String) body.get("managerComment")));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    @PostMapping("/{id}/acknowledge")
    public Map<String, Object> acknowledge(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            return toMap(service.acknowledge(id, user(authorization)));
        } catch (Exception e) {
            throw bad(e);
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            service.delete(id, user(authorization), role(authorization));
            return Map.of("success", true);
        } catch (Exception e) {
            throw bad(e);
        }
    }

    private List<Map<String, Object>> toMaps(List<PerformanceReview> reviews) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (reviews == null) return result;
        for (PerformanceReview review : reviews) {
            if (review != null) result.add(toMap(review));
        }
        return result;
    }

    private Map<String, Object> toMap(PerformanceReview review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("employeeName", review.getEmployeeName());
        map.put("userId", review.getUserId());
        map.put("cycle", review.getCycle());
        map.put("goal", review.getGoal());
        map.put("selfComment", review.getSelfComment());
        map.put("managerComment", review.getManagerComment());
        map.put("rating", review.getRating());
        map.put("status", review.getStatus());
        map.put("dueDate", review.getDueDate());
        map.put("createdAt", review.getCreatedAt());
        map.put("updatedAt", review.getUpdatedAt());
        return map;
    }
}
