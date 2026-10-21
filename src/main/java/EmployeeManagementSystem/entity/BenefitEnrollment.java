package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="benefit_enrollments", uniqueConstraints=@UniqueConstraint(columnNames={"userId","benefitPlan_id"}))
public class BenefitEnrollment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @ManyToOne(optional=false) private BenefitPlan benefitPlan;
 private String dependentName;
 private String dependentRelationship;
 private LocalDate enrollmentDate;
 private LocalDate effectiveDate;
 private String status;
 private String employeeComment;
 private String reviewedBy;
 private String reviewComment;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(enrollmentDate==null)enrollmentDate=LocalDate.now();if(status==null)status="ACTIVE";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
