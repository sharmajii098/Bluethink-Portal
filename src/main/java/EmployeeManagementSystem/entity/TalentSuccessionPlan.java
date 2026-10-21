package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="talent_succession_plans")
@Data
public class TalentSuccessionPlan {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true,nullable=false) private String planNumber;
    @Column(nullable=false) private String employeeUserId;
    private String employeeName;
    private String currentDesignation;
    private String currentDepartment;
    @Column(nullable=false) private String targetRole;
    private String criticalRole;
    @Column(nullable=false) private String readinessLevel;
    private String retentionRisk;
    private String performancePotential;
    @Column(length=2000) private String developmentActions;
    private String mentorUserId;
    @Transient
    private String mentorName;
    private String status;
    private String nominatedByUserId;
    private String nominatedByName;
    @Column(length=1000) private String reviewerComment;
    private String reviewerUserId;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void create(){createdAt=updatedAt=LocalDateTime.now(); if(status==null||status.isBlank())status="ACTIVE";}
    @PreUpdate void update(){updatedAt=LocalDateTime.now();}
}
