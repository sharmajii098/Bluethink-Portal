package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="employee_360_feedback", indexes={
 @Index(name="idx_feedback_subject", columnList="subjectUserId"),
 @Index(name="idx_feedback_reviewer", columnList="reviewerUserId"),
 @Index(name="idx_feedback_status", columnList="status")})
@Data
public class Employee360Feedback {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String feedbackNumber;
 @Column(nullable=false) private String subjectUserId;
 private String subjectName;
 @Column(nullable=false) private String reviewerUserId;
 private String reviewerName;
 @Column(nullable=false) private Integer rating;
 private String strengths;
 private String improvementAreas;
 private String comments;
 @Column(nullable=false) private String status;
 private String managementComment;
 private String reviewedBy;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){if(status==null)status="SUBMITTED";createdAt=LocalDateTime.now();updatedAt=createdAt;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
