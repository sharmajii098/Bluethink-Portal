package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="employee_referrals") @Getter @Setter @NoArgsConstructor
public class EmployeeReferral {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String referralNumber;
 @Column(nullable=false) private String referrerUserId; private String referrerName;
 @Column(nullable=false) private String candidateName; @Column(nullable=false) private String candidateEmail; private String candidatePhone;
 @Column(nullable=false) private String position; private String department; private String resumeReference;
 @Column(length=2000) private String note; @Column(nullable=false) private String status="SUBMITTED";
 private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
