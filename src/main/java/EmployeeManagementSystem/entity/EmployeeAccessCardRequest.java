package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name="employee_access_card_requests", indexes={@Index(name="idx_access_card_user",columnList="userId"),@Index(name="idx_access_card_status",columnList="status")}) @Data
public class EmployeeAccessCardRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String requestType;
 private String reason;
 private String accessLevel;
 private String cardNumber;
 private LocalDate requestedDate;
 private LocalDate issueDate;
 private LocalDate expiryDate;
 private String documentReference;
 private String status;
 private String reviewerUserId;
 private String reviewerComment;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ if(requestNumber==null) requestNumber="ACR-"+System.currentTimeMillis(); if(status==null) status="SUBMITTED"; if(requestedDate==null) requestedDate=LocalDate.now(); createdAt=LocalDateTime.now(); updatedAt=createdAt; }
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
