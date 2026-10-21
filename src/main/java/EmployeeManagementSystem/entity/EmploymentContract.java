package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name="employment_contracts") @Data
public class EmploymentContract {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(unique=true,nullable=false) private String contractNumber;
 private String userId; private String employeeName;
 private String contractType; private String designation; private String departmentName;
 private LocalDate startDate; private LocalDate endDate; private Integer noticeDays;
 private String workMode; private String employmentStatus;
 private String documentReference; private String renewalComment;
 private String status = "ACTIVE";
 private String reviewerUserId; private String reviewerComment;
 private LocalDateTime reviewedAt; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(contractNumber==null)contractNumber="CTR-"+System.currentTimeMillis();if(status==null)status="ACTIVE";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
