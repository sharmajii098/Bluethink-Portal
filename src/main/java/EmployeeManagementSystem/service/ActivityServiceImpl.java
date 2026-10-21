package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Activity;
import EmployeeManagementSystem.repository.ActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Page<Activity> getActivities(int page, int size) {
        return activityRepository.findAll(
                PageRequest.of(
                        Math.max(page, 0),
                        Math.min(Math.max(size, 1), 100),
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        );
    }

    @Override
    public Page<Activity> searchForUsers(
            Set<String> userIds,
            String employeeId,
            String action,
            String entityType,
            String username,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // FIX: Do not use Specification.where(null)
        Specification<Activity> spec = null;

        if (userIds != null) {
            if (userIds.isEmpty()) {
                return Page.empty(pageable);
            }

            Specification<Activity> userSpec =
                    (r, q, c) -> r.get("userId").in(userIds);

            spec = spec == null ? userSpec : spec.and(userSpec);
        }

        if (employeeId != null && !employeeId.isBlank()) {

            Specification<Activity> employeeSpec =
                    (r, q, c) ->
                            c.like(
                                    c.lower(r.<String>get("userId")),
                                    "%" + employeeId.toLowerCase() + "%"
                            );

            spec = spec == null ? employeeSpec : spec.and(employeeSpec);
        }

        if (action != null && !action.isBlank()) {

            Specification<Activity> actionSpec =
                    (r, q, c) ->
                            c.equal(
                                    c.upper(r.<String>get("action")),
                                    action.toUpperCase()
                            );

            spec = spec == null ? actionSpec : spec.and(actionSpec);
        }

        if (entityType != null && !entityType.isBlank()) {

            Specification<Activity> entitySpec =
                    (r, q, c) ->
                            c.equal(
                                    c.upper(r.<String>get("module")),
                                    entityType.toUpperCase()
                            );

            spec = spec == null ? entitySpec : spec.and(entitySpec);
        }

        if (username != null && !username.isBlank()) {

            Specification<Activity> usernameSpec =
                    (r, q, c) ->
                            c.like(
                                    c.lower(r.<String>get("status")),
                                    "%" + username.toLowerCase() + "%"
                            );

            spec = spec == null ? usernameSpec : spec.and(usernameSpec);
        }

        if (from != null) {

            Specification<Activity> fromSpec =
                    (r, q, c) ->
                            c.greaterThanOrEqualTo(
                                    r.<LocalDateTime>get("createdAt"),
                                    from
                            );

            spec = spec == null ? fromSpec : spec.and(fromSpec);
        }

        if (to != null) {

            Specification<Activity> toSpec =
                    (r, q, c) ->
                            c.lessThanOrEqualTo(
                                    r.<LocalDateTime>get("createdAt"),
                                    to
                            );

            spec = spec == null ? toSpec : spec.and(toSpec);
        }

        return activityRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Activity> search(
            String employeeId,
            String action,
            String entityType,
            String username,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {
        return searchForUsers(
                null,
                employeeId,
                action,
                entityType,
                username,
                from,
                to,
                page,
                size
        );
    }
}