package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.Employee;
import EmployeeManagementSystem.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Builds privacy-conscious birthday and work-anniversary cards for authenticated portal users. */
@Service
public class EmployeeMilestoneService {
    private final EmployeeRepository employees;

    public EmployeeMilestoneService(EmployeeRepository employees) {
        this.employees = employees;
    }

    public List<Map<String, Object>> upcoming(int days) {
        if (days < 1 || days > 366) {
            throw new IllegalArgumentException("days must be between 1 and 366.");
        }
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Employee employee : employees.findAllActiveEmployees()) {
            LocalDate birthday = employee.getDateOfBirth();
            if (birthday == null && employee.getEmployeeProfile() != null) birthday = employee.getEmployeeProfile().getDob();
            if (birthday != null) {
                LocalDate next = nextOccurrence(birthday, today);
                if (!next.isAfter(end)) out.add(item(employee, "BIRTHDAY", next, ChronoUnit.DAYS.between(today, next), "Birthday", null));
            }
            LocalDate joining = employee.getJoiningDate();
            if (joining != null && !joining.isAfter(today)) {
                LocalDate next = nextOccurrence(joining, today);
                int years = next.getYear() - joining.getYear();
                if (years > 0 && !next.isAfter(end)) {
                    out.add(item(employee, "WORK_ANNIVERSARY", next, ChronoUnit.DAYS.between(today, next),
                            years + (years == 1 ? " year work anniversary" : " years work anniversary"), years));
                }
            }
        }
        out.sort(Comparator.comparing((Map<String, Object> x) -> (LocalDate) x.get("date"))
                .thenComparing(x -> String.valueOf(x.get("employeeName")), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    public List<Map<String, Object>> month(int year, int month) {
        if (year < 1900 || year > 2200 || month < 1 || month > 12) {
            throw new IllegalArgumentException("A valid year and month are required.");
        }
        LocalDate first = LocalDate.of(year, month, 1);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Employee employee : employees.findAllActiveEmployees()) {
            LocalDate birthday = employee.getDateOfBirth();
            if (birthday == null && employee.getEmployeeProfile() != null) birthday = employee.getEmployeeProfile().getDob();
            if (birthday != null && birthday.getMonthValue() == month) {
                LocalDate date = safeDate(year, month, birthday.getDayOfMonth());
                out.add(item(employee, "BIRTHDAY", date, 0, "Birthday", null));
            }
            LocalDate joining = employee.getJoiningDate();
            if (joining != null && joining.getMonthValue() == month) {
                LocalDate date = safeDate(year, month, joining.getDayOfMonth());
                int years = year - joining.getYear();
                if (years > 0) out.add(item(employee, "WORK_ANNIVERSARY", date, 0,
                        years + (years == 1 ? " year work anniversary" : " years work anniversary"), years));
            }
        }
        out.sort(Comparator.comparing((Map<String, Object> x) -> (LocalDate) x.get("date"))
                .thenComparing(x -> String.valueOf(x.get("employeeName")), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    private LocalDate nextOccurrence(LocalDate source, LocalDate today) {
        LocalDate date = safeDate(today.getYear(), source.getMonthValue(), source.getDayOfMonth());
        return date.isBefore(today) ? safeDate(today.getYear() + 1, source.getMonthValue(), source.getDayOfMonth()) : date;
    }

    // A 29-February celebration is observed on 28 February in non-leap years.
    private LocalDate safeDate(int year, int month, int day) {
        try { return LocalDate.of(year, month, day); }
        catch (DateTimeException ex) { return LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth()); }
    }

    private Map<String, Object> item(Employee employee, String type, LocalDate date, long days, String label, Integer years) {
        Map<String, Object> result = new LinkedHashMap<>();
        String userId = employee.getRegisterEmployee() != null ? employee.getRegisterEmployee().getUserId() : null;
        if ((userId == null || userId.isBlank()) && employee.getEmployeeProfile() != null) {
            userId = employee.getEmployeeProfile().getUserId();
        }
        String name = employee.getFullName();
        if (name == null || name.isBlank()) {
            name = ((employee.getFirstName() == null ? "" : employee.getFirstName()) + " " +
                    (employee.getLastName() == null ? "" : employee.getLastName())).trim();
        }
        result.put("employeeId", employee.getId());
        result.put("userId", userId);
        result.put("employeeName", name.isBlank() ? "Employee" : name);
        result.put("department", employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null);
        result.put("type", type);
        result.put("date", date);
        result.put("daysUntil", days);
        result.put("label", label);
        if (years != null) result.put("yearsCompleted", years);
        return result;
    }
}
