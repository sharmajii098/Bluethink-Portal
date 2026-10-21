package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name="employee_suggestions", indexes={@Index(name="idx_suggestion_user", columnList="userId"), @Index(name="idx_suggestion_status", columnList="status")})
@Data
public class EmployeeSuggestion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String suggestionNumber;
    @Column(nullable=false) private String userId;
    private String employeeName;
    @Column(nullable=false) private String category;
    @Column(nullable=false) private String subject;
    @Column(nullable=false, length=4000) private String description;
    private String priority;
    private boolean anonymous;
    private String status;
    private String managementResponse;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void prePersist(){ if(status==null) status="SUBMITTED"; if(priority==null) priority="MEDIUM"; createdAt=LocalDateTime.now(); updatedAt=createdAt; }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
