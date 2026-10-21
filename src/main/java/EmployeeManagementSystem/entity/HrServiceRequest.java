package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="hr_service_requests")
@Getter @Setter @NoArgsConstructor
public class HrServiceRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String requestNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String category;
 @Column(nullable=false) private String subject;
 @Column(length=4000, nullable=false) private String description;
 @Column(nullable=false) private String priority="MEDIUM";
 @Column(nullable=false) private String status="SUBMITTED";
 private String assignedTo;
 private String assignedTeam;
 private LocalDateTime slaDueAt;
 @Column(length=3000) private String resolution;
 private String reviewerUserId;
 private String reviewerComment;
 private LocalDateTime reviewedAt;
 private LocalDateTime resolvedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){
  if(createdAt==null) createdAt=LocalDateTime.now();
  updatedAt=createdAt;
  if(priority==null||priority.isBlank()) priority="MEDIUM";
  if(status==null||status.isBlank()) status="SUBMITTED";
 }
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
