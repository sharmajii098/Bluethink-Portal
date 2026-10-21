package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.entity.Notification;
import EmployeeManagementSystem.repository.EmployeeRepository;
import EmployeeManagementSystem.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Creates persistent, per-recipient birthday and work-anniversary notifications. */
@Service
@RequiredArgsConstructor
public class CelebrationNotificationScheduler {
    private final EmployeeRepository employees;
    private final NotificationRepository notifications;
    private final NotificationSseService sse;

    // Run on startup as well as daily, so today's events appear without waiting until tomorrow.
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() { createTodaysCelebrations(); }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void scheduledRun() { createTodaysCelebrations(); }

    @Transactional
    public void createTodaysCelebrations() {
        LocalDate today = LocalDate.now();
        List<Employee> all = employees.findAllActiveEmployees();
        for (Employee celebrant : all) {
            if (celebrant == null) continue;
            String name = celebrant.getFullName();
            if (name == null || name.isBlank()) name = ((celebrant.getFirstName() == null ? "" : celebrant.getFirstName()) + " " + (celebrant.getLastName() == null ? "" : celebrant.getLastName())).trim();
            if (name.isBlank()) name = "A colleague";
            LocalDate birthday = celebrant.getDateOfBirth();
            if (birthday == null && celebrant.getEmployeeProfile() != null) {
                birthday = celebrant.getEmployeeProfile().getDob();
            }
            LocalDate eventDate = birthday == null ? null : nextOccurrence(birthday, today);
            String message = null;
            if (eventDate != null && !eventDate.isBefore(today) && !eventDate.isAfter(today.plusDays(7))) {
                message = eventDate.equals(today)
                        ? "🎂 Today is " + name + "'s birthday! Send your wishes and make their day special."
                        : "🎂 " + name + "'s birthday is on " + eventDate + ". Send your wishes!";
            } else if (celebrant.getJoiningDate() != null && !celebrant.getJoiningDate().isAfter(today)) {
                LocalDate joining = celebrant.getJoiningDate();
                eventDate = nextOccurrence(joining, today);
                int years = eventDate.getYear() - joining.getYear();
                if (years > 0 && !eventDate.isAfter(today.plusDays(7))) {
                    message = eventDate.equals(today)
                            ? "🏆 Congratulations to " + name + " on completing " + years + (years == 1 ? " year" : " years") + " with us! Send your wishes."
                            : "🏆 " + name + " completes " + years + (years == 1 ? " year" : " years") + " with us on " + eventDate + ". Send your wishes!";
                }
            }
            if (message == null) continue;
            for (Employee recipient : all) {
                if (recipient == null) continue;
                String userId = recipient.getRegisterEmployee() != null ? recipient.getRegisterEmployee().getUserId() : null;
                if ((userId == null || userId.isBlank()) && recipient.getEmployeeProfile() != null) {
                    userId = recipient.getEmployeeProfile().getUserId();
                }
                if (userId == null || userId.isBlank()) continue;
                if (notifications.existsByRecipientUserIdAndMessage(userId, message)) continue;
                Notification n = new Notification();
                n.setEmployee(celebrant); // event subject; recipientUserId controls inbox visibility
                n.setRecipientUserId(userId);
                n.setMessage(message);
                n.setCreatedAt(LocalDateTime.now());
                n.setRead(false);
                Notification saved = notifications.save(n);
                sse.publish(saved);
            }
        }
    }

    private LocalDate nextOccurrence(LocalDate source, LocalDate today) {
        int month = source.getMonthValue();
        int day = source.getDayOfMonth();
        LocalDate next = safeDate(today.getYear(), month, day);
        return next.isBefore(today) ? safeDate(today.getYear() + 1, month, day) : next;
    }

    private LocalDate safeDate(int year, int month, int day) {
        try { return LocalDate.of(year, month, day); }
        catch (java.time.DateTimeException ex) {
            return LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth());
        }
    }
}
