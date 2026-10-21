package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="internal_job_applications", uniqueConstraints=@UniqueConstraint(columnNames={"posting_id","applicantUserId"})) @Getter @Setter @NoArgsConstructor
public class InternalJobApplication {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) private InternalJobPosting posting;
 @Column(nullable=false) private String applicantUserId; private String applicantName; private String currentDesignation; private String currentDepartment;
 @Column(length=2000) private String coverNote; private String resumeReference;
 @Column(nullable=false) private String status="SUBMITTED"; private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="SUBMITTED";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
