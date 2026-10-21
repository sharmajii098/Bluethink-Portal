package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name="employee_flexible_work_requests", indexes={
 @Index(name="idx_flex_user", columnList="userId"),
 @Index(name="idx_flex_status", columnList="status")})
@Getter @Setter @NoArgsConstructor
public class EmployeeFlexibleWorkRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String requestNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String arrangementType;
 private LocalDate effectiveDate;
 private LocalDate endDate;
 private String officeDays;
 private String remoteDays;
 private String preferredHours;
 private String reason;
 @Column(length=2000) private String businessImpact;
 private String status="SUBMITTED";
 private String reviewerUserId;
 private String reviewerComment;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null) status="SUBMITTED"; }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
