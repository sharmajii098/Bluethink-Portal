package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "timesheet_locks", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "lock_date"}))
public class TimesheetLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "employee_role", nullable = false)
    private String employeeRole;

    @Column(name = "lock_date", nullable = false)
    private LocalDate lockDate;

    private LocalTime shiftStartTime;
    private LocalTime cutoffTime;
    private LocalDateTime firstLoginTime;

    @Column(nullable = false)
    private String status = "BLOCKED";

    private String unlockedBy;
    private String unlockedByRole;
    private LocalDateTime unlockedAt;

    @Column(length = 1000)
    private String unlockReason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
