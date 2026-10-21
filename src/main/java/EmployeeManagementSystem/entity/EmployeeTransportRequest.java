package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="employee_transport_requests") @Getter @Setter @NoArgsConstructor
public class EmployeeTransportRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId; private String employeeName;
 @Column(nullable=false) private String requestType; private String pickupLocation; private String dropLocation;
 private String shiftTiming; private String transportMode; private String routePreference;
 private LocalDate effectiveDate; private LocalDate endDate; private String priority="MEDIUM";
 @Column(length=2000) private String reason; private String status="SUBMITTED";
 private String assignedTo; private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";if(priority==null)priority="MEDIUM";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
