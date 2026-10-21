package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="employee_policy_acknowledgements", indexes={
 @Index(name="idx_policy_ack_user", columnList="userId"),
 @Index(name="idx_policy_ack_status", columnList="status"),
 @Index(name="idx_policy_ack_due", columnList="dueDate")
})
@Data
public class EmployeePolicyAcknowledgement {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String acknowledgementNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String title;
 private String policyType;
 private String version;
 @Column(length=4000) private String description;
 private boolean mandatory;
 private LocalDate dueDate;
 private String status;
 private String acknowledgementNote;
 private LocalDateTime acknowledgedAt;
 private String reviewedBy;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){if(status==null)status="PENDING";createdAt=LocalDateTime.now();updatedAt=createdAt;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
