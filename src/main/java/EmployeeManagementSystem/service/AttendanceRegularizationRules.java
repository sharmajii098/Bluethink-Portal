package EmployeeManagementSystem.service;

import EmployeeManagementSystem.enums.AttendanceStatus;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Validation and calculations shared by request submission and approval. */
public final class AttendanceRegularizationRules {
    private AttendanceRegularizationRules() { }

    public record Correction(LocalDateTime checkIn, LocalTime checkOut,
                             double workingHours, AttendanceStatus status) { }

    public static Correction calculate(LocalDate date, String requestedIn, String requestedOut,
                                       LocalDateTime now, LocalTime officeStart, int graceMinutes) {
        if (date == null || date.isAfter(now.toLocalDate()))
            throw new IllegalArgumentException("Select a valid attendance date, not a future date.");
        if (requestedIn == null || requestedIn.isBlank() || requestedOut == null || requestedOut.isBlank())
            throw new IllegalArgumentException("Both requested check-in and check-out times are required.");
        final LocalDateTime in;
        final LocalTime outTime;
        try {
            in = LocalDateTime.parse(requestedIn.trim());
            outTime = LocalTime.parse(requestedOut.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Use a valid check-in date/time and check-out time.");
        }
        if (!in.toLocalDate().equals(date))
            throw new IllegalArgumentException("Requested check-in must be on the attendance date.");
        // The current UI requests a time-only checkout; overnight shifts require a separate
        // date-aware form, so do not silently credit an overnight interval here.
        LocalDateTime out = LocalDateTime.of(date, outTime);
        long minutes = Duration.between(in, out).toMinutes();
        if (minutes <= 0 || out.isAfter(now))
            throw new IllegalArgumentException("Checkout must be after check-in and cannot be in the future. Overnight shifts require a date-aware request.");
        if (officeStart == null || graceMinutes < 0 || graceMinutes > 1440)
            throw new IllegalArgumentException("The office start or grace-period setting is invalid.");
        double hours = Math.round(minutes / 60.0 * 100.0) / 100.0;
        AttendanceStatus status = in.toLocalTime().isAfter(officeStart.plusMinutes(graceMinutes))
                ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        return new Correction(in, outTime, hours, status);
    }
}
