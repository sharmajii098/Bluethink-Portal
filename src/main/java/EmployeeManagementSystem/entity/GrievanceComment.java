package EmployeeManagementSystem.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter; import java.time.LocalDateTime;
@Entity @Table(name="grievance_comments") @Getter @Setter
public class GrievanceComment { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="case_id") private GrievanceCase grievanceCase; @Column(nullable=false) private String authorUserId; private String authorName; @Column(length=4000,nullable=false) private String comment; private LocalDateTime createdAt; @PrePersist void pre(){createdAt=LocalDateTime.now();} }
