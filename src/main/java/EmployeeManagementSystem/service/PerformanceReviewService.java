package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.PerformanceReview;
import java.util.*;

public interface PerformanceReviewService {
    List<PerformanceReview> list(String userId, String role);
    PerformanceReview create(String userId, String cycle, String goal, String dueDate);
    PerformanceReview updateSelf(Long id, String userId, String selfComment, String goal, String dueDate);
    PerformanceReview submit(Long id, String userId);
    PerformanceReview review(Long id, String managerUserId, Integer rating, String comment);
    PerformanceReview acknowledge(Long id, String userId);
    void delete(Long id, String userId, String role);
    PerformanceReview get(Long id);
}
