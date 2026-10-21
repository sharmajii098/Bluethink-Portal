package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="payroll_change_requests") @Getter @Setter @NoArgsConstructor
public class PayrollChangeRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId; private String employeeName;
 @Column(nullable=false) private String changeType;
 private String bankName; private String accountHolderName; private String accountNumber; private String ifscCode;
 private String paymentMethod; private String taxRegime;
 @Column(length=3000) private String reason;
 @Column(nullable=false) private String status="SUBMITTED";
 private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
