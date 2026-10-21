package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Data @Table(name="loan_advances")
public class LoanAdvance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId; private String employeeName; private String type; private String purpose;
 @Column(nullable=false,precision=14,scale=2) private BigDecimal amount;
 @Column(precision=14,scale=2) private BigDecimal approvedAmount; private Integer tenureMonths; private BigDecimal monthlyDeduction;
 private LocalDate requestedDate; private LocalDate approvedDate; private LocalDate firstDeductionDate; private String status; private String reviewer; private String reviewComment;
 private BigDecimal outstandingBalance; private Integer paidInstallments; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(requestedDate==null)requestedDate=LocalDate.now();if(status==null)status="PENDING";if(outstandingBalance==null)outstandingBalance=amount;if(paidInstallments==null)paidInstallments=0;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
