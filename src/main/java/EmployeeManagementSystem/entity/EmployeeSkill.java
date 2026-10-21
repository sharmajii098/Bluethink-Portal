package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="employee_skills", indexes={@Index(name="idx_skill_user", columnList="userId"), @Index(name="idx_skill_name", columnList="skillName")})
@Data
public class EmployeeSkill {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String recordNumber;
    @Column(nullable=false) private String userId;
    private String employeeName;
    @Column(nullable=false) private String skillName;
    private String category;
    private String proficiency;
    private Integer yearsOfExperience;
    private String lastUsed;
    private String certificationName;
    private String certificationReference;
    private String notes;
    private String status;
    private String reviewerUserId;
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void prePersist(){ if(status==null) status="PENDING"; createdAt=LocalDateTime.now(); updatedAt=createdAt; }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
