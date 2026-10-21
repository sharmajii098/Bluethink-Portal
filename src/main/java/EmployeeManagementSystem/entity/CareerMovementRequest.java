package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Data @Table(name="career_movement_requests") public class CareerMovementRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String employeeUserId; private String employeeName;
 private String currentDesignation; @Column(nullable=false) private String proposedDesignation;
 @Column(length=2500) private String reason; private String careerGoal; private String priority="NORMAL";
 private String status="SUBMITTED"; private LocalDate requestedEffectiveDate;
 private String managerComment; private String hrComment; private String reviewedBy; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";if(priority==null)priority="NORMAL";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
