package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.time.LocalDateTime;
@Entity @Data @Table(name="employee_recognitions") public class EmployeeRecognition {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String recognitionNumber;
 @Column(nullable=false) private String nomineeUserId; private String nomineeName;
 @Column(nullable=false) private String nominatedByUserId; private String nominatedByName;
 @Column(nullable=false) private String awardType; private String title;
 @Column(length=2000) private String message; private Integer points=0;
 private String status="PENDING"; private String reviewerUserId; private String reviewComment; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(points==null)points=0;if(status==null)status="PENDING";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
