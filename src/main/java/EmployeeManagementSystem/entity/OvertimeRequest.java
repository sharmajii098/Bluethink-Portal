package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.time.*;
@Entity @Data @Table(name="overtime_requests")
public class OvertimeRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String requestNumber;
 @Column(nullable=false) private String userId; private String employeeName;
 @Column(nullable=false) private LocalDate workDate;
 @Column(nullable=false) private Double hours;
 @Column(nullable=false) private String requestType="OVERTIME";
 private String project; @Column(length=2000) private String reason;
 private String compensationMode="PAYMENT"; private Double compOffDays=0.0;
 private String status="SUBMITTED"; private String reviewerUserId; private String reviewerComment;
 private LocalDateTime reviewedAt; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";if(compOffDays==null)compOffDays=0.0;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
