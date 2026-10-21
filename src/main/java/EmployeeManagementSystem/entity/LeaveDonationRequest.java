package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name="leave_donation_requests")
@Getter @Setter @NoArgsConstructor
public class LeaveDonationRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String requestNumber;
    @Column(nullable=false) private String donorUserId;
    private String donorName;
    @Column(nullable=false) private String recipientUserId;
    private String recipientName;
    private String leaveType="PAID_LEAVE";
    private Integer days;
    @Column(length=2000) private String reason;
    @Column(nullable=false) private String status="SUBMITTED";
    private String reviewerUserId;
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    /** True only after donor/recipient balances have been updated successfully. */
    private Boolean transferApplied = false;
    /** Calendar year whose leave-balance buckets were adjusted. */
    private Integer transferYear;
    private LocalDateTime transferredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";if(transferApplied==null)transferApplied=false;}
    @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
