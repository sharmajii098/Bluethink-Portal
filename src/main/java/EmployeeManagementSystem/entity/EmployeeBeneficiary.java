package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name="employee_beneficiaries")
@Getter @Setter @NoArgsConstructor
public class EmployeeBeneficiary {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String recordNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String benefitType;
 @Column(nullable=false) private String nomineeName;
 private String relationship;
 private LocalDate dateOfBirth;
 @Column(precision=5,scale=2,nullable=false) private BigDecimal allocationPercentage=BigDecimal.ZERO;
 private String phone;
 private String email;
 @Column(length=2000) private String address;
 private String guardianName;
 private String guardianRelationship;
 @Column(length=2000) private String notes;
 @Column(nullable=false) private String status="PENDING";
 private String reviewerUserId;
 private String reviewerComment;
 private LocalDateTime reviewedAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="PENDING";if(allocationPercentage==null)allocationPercentage=BigDecimal.ZERO;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
