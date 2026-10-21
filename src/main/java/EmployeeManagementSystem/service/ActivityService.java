package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Activity;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Set;

public interface ActivityService {

    Page<Activity> getActivities(int page, int size);

    Page<Activity> searchForUsers(Set<String> userIds, String employeeId, String action, String entityType, String username, LocalDateTime from, LocalDateTime to, int page, int size);

    Page<Activity> search(
            String employeeId,
            String action,
            String entityType,
            String username,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    );
}