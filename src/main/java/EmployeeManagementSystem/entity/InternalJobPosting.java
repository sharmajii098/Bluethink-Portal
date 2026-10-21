package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="internal_job_postings") @Getter @Setter @NoArgsConstructor
public class InternalJobPosting {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String postingNumber;
 @Column(nullable=false) private String title; @Column(nullable=false,length=3000) private String description;
 private String department; private String location; private String employmentType; private String requiredSkills;
 private LocalDate applicationDeadline; @Column(nullable=false) private String status="OPEN";
 private String createdByUserId; private String createdByName; private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="OPEN";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
