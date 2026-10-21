package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="employee_shift_preference_requests") @Getter @Setter @NoArgsConstructor
public class EmployeeShiftPreferenceRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId; private String employeeName;
 @Column(nullable=false) private String preferredShift;
 private String preferredDays; private LocalDate effectiveDate;
 @Column(length=2000) private String reason;
 private String status="SUBMITTED"; private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
