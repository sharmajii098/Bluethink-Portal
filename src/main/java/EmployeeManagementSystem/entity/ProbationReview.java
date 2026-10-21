package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="probation_reviews")
public class ProbationReview {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String reviewNumber;
 @Column(nullable=false) private String employeeUserId;
 private String employeeName;
 private Long employeeId;
 private LocalDate joiningDate;
 private LocalDate probationEndDate;
 private String currentDesignation;
 private String departmentName;
 private String reviewerUserId;
 private String reviewerName;
 private String status="PENDING";
 private Integer rating;
 @Column(length=2500) private String employeeComment;
 @Column(length=2500) private String reviewerComment;
 private LocalDate decisionDate;
 private LocalDate confirmationDate;
 private String decision="";
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null)status="PENDING"; }
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
