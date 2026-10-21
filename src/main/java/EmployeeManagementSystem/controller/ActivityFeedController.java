package EmployeeManagementSystem.controller;

import EmployeeManagementSystem.entity.Activity;
import EmployeeManagementSystem.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity-feed")
public class ActivityFeedController {
    private final ActivityRepository activityRepository;

    public ActivityFeedController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Activity>> getActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        String q = keyword == null ? "" : keyword.trim();
        String s = status == null ? "" : status.trim();
        Page<Activity> result;
        if (!q.isEmpty() && !s.isEmpty()) {
            result = activityRepository.findByEmployeeNameContainingIgnoreCaseAndStatusIgnoreCase(q, s, pageable);
        } else if (!q.isEmpty()) {
            result = activityRepository.findByEmployeeNameContainingIgnoreCaseOrActivityContainingIgnoreCase(q, q, pageable);
        } else if (!s.isEmpty()) {
            result = activityRepository.findByStatusIgnoreCase(s, pageable);
        } else {
            result = activityRepository.findAll(pageable);
        }
        return ResponseEntity.ok(result);
    }
}
