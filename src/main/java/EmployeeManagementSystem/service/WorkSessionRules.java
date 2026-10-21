package EmployeeManagementSystem.service;
import java.time.Duration;
import java.time.LocalDateTime;
/** Deterministic policy logic; unit-tested without Spring or a database. */
public final class WorkSessionRules {
    private WorkSessionRules() { }
    public static boolean expired(LocalDateTime start, LocalDateTime now) {
        return start != null && now != null && !start.plusHours(24).isAfter(now);
    }
    public static long creditedMinutes(LocalDateTime start, LocalDateTime end, String status) {
        if ("AUTO_CLOSED_UNVERIFIED".equals(status) || start == null || end == null || end.isBefore(start)) return 0;
        return Math.max(0, Duration.between(start, end).toMinutes());
    }
    public static long remainingMinutes(long verifiedMinutes) { return Math.max(0, 540 - verifiedMinutes); }
}
