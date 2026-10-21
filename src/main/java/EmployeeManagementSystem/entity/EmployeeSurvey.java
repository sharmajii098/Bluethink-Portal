package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Data; import java.time.LocalDateTime;
@Entity @Data @Table(name="employee_surveys") public class EmployeeSurvey {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String surveyNumber;
 @Column(nullable=false) private String title;
 @Column(length=2000) private String description;
 @Column(nullable=false) private String question;
 private String category="Engagement"; private String status="DRAFT"; private Boolean anonymous=true;
 private LocalDateTime startAt; private LocalDateTime endAt; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="DRAFT";if(anonymous==null)anonymous=true;}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
