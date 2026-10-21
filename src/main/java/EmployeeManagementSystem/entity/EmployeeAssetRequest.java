package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="employee_asset_requests") @Getter @Setter @NoArgsConstructor
public class EmployeeAssetRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId; private String employeeName;
 @Column(nullable=false) private String assetType; private String specification;
 @Column(length=2000) private String reason; private String priority="MEDIUM";
 private LocalDate requiredBy; private String status="SUBMITTED";
 private String assignedTo; private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt;
 private LocalDateTime issuedAt; private String assetReference;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";if(priority==null)priority="MEDIUM";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
