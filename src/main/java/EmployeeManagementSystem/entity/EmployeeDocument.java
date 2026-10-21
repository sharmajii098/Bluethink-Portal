package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name="employee_documents") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeDocument {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String employeeId;
 @Column(nullable=false) private String employeeName;
 @Column(nullable=false) private String category;
 @Column(nullable=false) private String originalFilename;
 @Column(nullable=false, unique=true) private String storedFilename;
 private String contentType;
 private Long fileSize;
 @Column(nullable=false) private String status;
 private LocalDate issueDate;
 private LocalDate expiryDate;
 @Column(columnDefinition="TEXT") private String description;
 private String uploadedBy;
 private LocalDateTime uploadedAt;
 private String verifiedBy;
 private LocalDateTime verifiedAt;
 @Column(columnDefinition="TEXT") private String reviewComment;
 @PrePersist void created(){ if(uploadedAt==null) uploadedAt=LocalDateTime.now(); if(status==null) status="PENDING"; }
}
