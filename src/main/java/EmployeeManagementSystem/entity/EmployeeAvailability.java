package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="employee_availability", indexes={
 @Index(name="idx_availability_user", columnList="userId"),
 @Index(name="idx_availability_dates", columnList="startDate,endDate"),
 @Index(name="idx_availability_status", columnList="status")})
@Data
public class EmployeeAvailability {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String availabilityNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private LocalDate startDate;
 @Column(nullable=false) private LocalDate endDate;
 @Column(nullable=false) private String availabilityType;
 private String reason;
 private String delegateUserId;
 private String delegateName;
 private String message;
 private String availableFrom;
 private String availableTill;
 private Boolean notifyManager;
 private Boolean enableAutoReply;
 private Boolean showOnCalendar;
 private String status;
 private String reviewerUserId;
 private String reviewerComment;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ if(status==null) status="SUBMITTED"; createdAt=LocalDateTime.now(); updatedAt=createdAt; }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
