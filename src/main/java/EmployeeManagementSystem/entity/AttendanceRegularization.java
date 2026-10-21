package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="attendance_regularizations")
public class AttendanceRegularization {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String requestNumber;
 @Column(nullable=false) private String employeeUserId;
 private String employeeName;
 private LocalDate attendanceDate;
 private String requestedCheckIn;
 private String requestedCheckOut;
 private String reason;
 private String status="SUBMITTED";
 private String reviewerUserId;
 private String reviewerName;
 @Column(length=1500) private String reviewerComment;
 private LocalDateTime reviewedAt;
 /** Set only when the approved correction is actually saved in the attendance table. */
 private Long appliedAttendanceId;
 private LocalDateTime appliedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null) status="SUBMITTED"; }
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
