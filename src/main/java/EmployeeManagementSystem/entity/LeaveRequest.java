package EmployeeManagementSystem.entity;

import EmployeeManagementSystem.enums.LeaveStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;

    /** Leave category, e.g. Casual Leave / Sick Leave / Earned Leave. */
    private String leaveType;

    /** FULL_DAY, FIRST_HALF or SECOND_HALF. */
    @Column(length = 30)
    private String leaveDurationType = "FULL_DAY";

    /** Optional comma-separated recipients copied on the leave request. */
    @Column(length = 1000)
    private String ccEmailTo;

    /** Snapshot of reporting-manager details at request time. */
    private String reportingManagerName;
    private String reportingManagerEmail;
    /** Validated, explicitly selected reporting manager email for notifications. */
    @Column(length = 254)
    private String managerNotificationEmail;

    @Column(length = 1000)
    private String reason;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(length = 1000)
    private String approvalComment;

    private LocalDateTime approvedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public String getLeaveDurationType() { return leaveDurationType; }
    public void setLeaveDurationType(String leaveDurationType) { this.leaveDurationType = leaveDurationType; }
    public String getCcEmailTo() { return ccEmailTo; }
    public void setCcEmailTo(String ccEmailTo) { this.ccEmailTo = ccEmailTo; }
    public String getReportingManagerName() { return reportingManagerName; }
    public void setReportingManagerName(String reportingManagerName) { this.reportingManagerName = reportingManagerName; }
    public String getManagerNotificationEmail() { return managerNotificationEmail; }
    public void setManagerNotificationEmail(String managerNotificationEmail) { this.managerNotificationEmail = managerNotificationEmail; }
    public String getReportingManagerEmail() { return reportingManagerEmail; }
    public void setReportingManagerEmail(String reportingManagerEmail) { this.reportingManagerEmail = reportingManagerEmail; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public String getApprovalComment() { return approvalComment; }
    public void setApprovalComment(String approvalComment) { this.approvalComment = approvalComment; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    /** Number of leave days charged to the balance. */
    public double getTotalDays() {
        if (startDate == null || endDate == null) return 0.0;
        String duration = leaveDurationType == null ? "FULL_DAY" : leaveDurationType.toUpperCase();
        if ("FIRST_HALF".equals(duration) || "SECOND_HALF".equals(duration)) return 0.5;
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1.0;
    }
}
