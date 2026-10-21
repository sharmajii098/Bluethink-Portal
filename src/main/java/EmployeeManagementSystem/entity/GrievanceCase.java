package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Table(name="grievance_cases") @Getter @Setter
public class GrievanceCase {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String caseNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String category;
 @Column(nullable=false) private String subject;
 @Column(length=6000,nullable=false) private String description;
 private String priority;
 private String status;
 private boolean confidential=true;
 private String assignedTo;
 private String assignedTeam;
 private LocalDateTime slaDueAt;
 @Column(length=6000) private String resolution;
 private LocalDateTime resolvedAt;
 @Column(length=2000) private String evidenceReference;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=updatedAt=LocalDateTime.now(); if(priority==null)priority="MEDIUM"; if(status==null)status="OPEN"; if(assignedTeam==null)assignedTeam="HR";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
