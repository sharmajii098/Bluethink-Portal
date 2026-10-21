package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.*;

@Entity
@Data
@Table(name = "shift_swap_requests")
public class ShiftSwapRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true) private String requestNumber;
    @Column(nullable=false) private String requesterUserId;
    private String requesterName;
    @Column(nullable=false) private String targetUserId;
    private String targetName;
    @Column(nullable=false) private LocalDate requesterDate;
    @Column(nullable=false) private LocalDate targetDate;
    private String requesterShift;
    private String targetShift;
    /** Assignment + shift snapshots for conflict detection. Existing rows may have null snapshots. */
    private Long requesterAssignmentId;
    private Long targetAssignmentId;
    private Long requesterShiftId;
    private Long targetShiftId;
    /** PENDING, ACCEPTED or DECLINED. Legacy null is treated as pending. */
    private String targetConsentStatus = "PENDING";
    private String targetConsentComment;
    private LocalDateTime targetRespondedAt;
    @Column(length=2000) private String reason;
    @Column(nullable=false) private String status = "SUBMITTED";
    private String reviewerUserId;
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist void pre(){
        if(createdAt==null) createdAt=LocalDateTime.now();
        updatedAt=createdAt;
        if(status==null) status="SUBMITTED";
    }
    @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
