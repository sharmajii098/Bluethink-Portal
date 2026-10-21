package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_encashment_requests")
@Getter @Setter @NoArgsConstructor
public class LeaveEncashmentRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String requestNumber;
    @Column(nullable=false)
    private String userId;
    private String employeeName;
    @Column(nullable=false)
    private String financialYear;
    @Column(nullable=false)
    private String leaveType;
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal requestedDays = BigDecimal.ZERO;
    @Column(precision=10, scale=2)
    private BigDecimal approvedDays = BigDecimal.ZERO;
    @Column(precision=15, scale=2)
    private BigDecimal estimatedAmount = BigDecimal.ZERO;
    @Column(length=2000)
    private String reason;
    @Column(nullable=false)
    private String status = "SUBMITTED";
    private String reviewerUserId;
    @Column(length=2000)
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist(){
        if(createdAt==null) createdAt=LocalDateTime.now();
        updatedAt=createdAt;
        if(status==null) status="SUBMITTED";
        if(requestedDays==null) requestedDays=BigDecimal.ZERO;
        if(approvedDays==null) approvedDays=BigDecimal.ZERO;
        if(estimatedAmount==null) estimatedAmount=BigDecimal.ZERO;
    }
    @PreUpdate
    void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
