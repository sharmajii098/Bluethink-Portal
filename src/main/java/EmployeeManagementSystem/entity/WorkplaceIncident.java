package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.*; import java.time.*;
@Entity @Table(name="workplace_incidents") @Getter @Setter @NoArgsConstructor
public class WorkplaceIncident {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String incidentNumber;
 @Column(nullable=false) private String reporterUserId; private String reporterName;
 private String incidentType; private String severity; private LocalDate incidentDate; private String incidentTime; private String location;
 @Column(length=3000) private String description; private String immediateAction; private String witnesses;
 private String status="REPORTED"; private String assignedTo; private String correctiveAction;
 private String reviewerUserId; private String reviewerComment; private LocalDateTime reviewedAt; private LocalDateTime resolvedAt;
 private LocalDateTime createdAt; private LocalDateTime updatedAt;
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();updatedAt=createdAt;if(status==null)status="REPORTED";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
