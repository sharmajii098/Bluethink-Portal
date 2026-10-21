package EmployeeManagementSystem.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Set;

/** Pure guard rules shared by stand-up-to-timesheet synchronization. No database dependencies. */
public final class StandupTimesheetPolicy {
 private StandupTimesheetPolicy() {}
 public static boolean validHours(Double hours) {
  return hours != null && Double.isFinite(hours) && hours >= .25 && hours <= 24
    && Math.rint(hours * 4) == hours * 4;
 }
 public static boolean editable(String status) {
  return status != null && Set.of("PENDING", "REJECTED").contains(status.toUpperCase(Locale.ROOT));
 }
 public static boolean withinCutoff(LocalDate workDate, LocalDate today) {
  return workDate != null && today != null && !workDate.isAfter(today)
    && today.isBefore(YearMonth.from(workDate).plusMonths(1).atDay(2));
 }
}
