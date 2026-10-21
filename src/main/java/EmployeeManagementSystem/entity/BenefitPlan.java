package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="benefit_plans")
public class BenefitPlan {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String planCode;
 @Column(nullable=false) private String planName;
 @Column(nullable=false) private String category;
 private String provider;
 private Double employeeContribution;
 private Double employerContribution;
 private String coverageSummary;
 private LocalDate enrollmentStart;
 private LocalDate enrollmentEnd;
 private LocalDate effectiveFrom;
 private LocalDate effectiveTo;
 private Boolean active=true;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(active==null)active=true;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
