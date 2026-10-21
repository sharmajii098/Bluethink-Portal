package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_privacy_consents")
@Getter @Setter @NoArgsConstructor
public class EmployeePrivacyConsent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String consentNumber;
    @Column(nullable=false) private String userId;
    private String employeeName;
    @Column(nullable=false) private String consentType;
    @Column(nullable=false) private String title;
    @Column(length=2000) private String description;
    @Column(nullable=false) private String policyVersion;
    private Boolean mandatory = false;
    @Column(nullable=false) private String status = "PENDING";
    private LocalDateTime consentedAt;
    private LocalDateTime withdrawnAt;
    private String source = "EMPLOYEE_PORTAL";
    @Column(length=1000) private String employeeComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void pre(){
        if(createdAt==null) createdAt=LocalDateTime.now();
        updatedAt=createdAt;
        if(mandatory==null) mandatory=false;
        if(status==null) status="PENDING";
        if(source==null) source="EMPLOYEE_PORTAL";
    }
    @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
