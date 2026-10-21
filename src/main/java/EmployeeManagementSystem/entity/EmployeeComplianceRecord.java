package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity @Table(name="employee_compliance_records") @Getter @Setter @NoArgsConstructor
public class EmployeeComplianceRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String recordNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String documentType;
 @Column(nullable=false) private String documentName;
 private String documentNumber;
 private LocalDate issueDate;
 private LocalDate expiryDate;
 private String issuingAuthority;
 @Column(length=2000) private String documentReference;
 @Column(length=2000) private String notes;
 @Column(nullable=false) private String status="PENDING_VERIFICATION";
 private String reviewerUserId;
 private String reviewerComment;
 private LocalDateTime verifiedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="PENDING_VERIFICATION";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
