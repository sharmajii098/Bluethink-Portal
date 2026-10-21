package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.AttendanceTracking;
import EmployeeManagementSystem.repository.AttendanceTrackingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/** Incomplete sign-offs are unverified. Never credit 24 unattended hours as payroll time. */
@Service
public class ExpiredWorkSessionService {
    private final AttendanceTrackingRepository repository;
    public ExpiredWorkSessionService(AttendanceTrackingRepository repository) { this.repository = repository; }

    @Scheduled(fixedDelay = 60000L)
    @Transactional
    public void closeExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        for (AttendanceTracking row : repository.findByLogoutTimeIsNullAndLoginTimeLessThanEqual(now.minusHours(24))) {
            expire(row);
        }
    }

    @Transactional
    public void closeExpiredForUser(String userId) {
        LocalDateTime now = LocalDateTime.now();
        for (AttendanceTracking row : repository.findByEmployeeIdAndLogoutTimeIsNull(userId)) {
            if (WorkSessionRules.expired(row.getLoginTime(), now)) expire(row);
        }
    }

    private void expire(AttendanceTracking row) {
        if (row.getLogoutTime() != null || row.getLoginTime() == null) return;
        row.setLogoutTime(row.getLoginTime().plusHours(24));
        row.setWorkingHours(0.0);
        row.setStatus("AUTO_CLOSED_UNVERIFIED");
        repository.save(row);
    }
}
