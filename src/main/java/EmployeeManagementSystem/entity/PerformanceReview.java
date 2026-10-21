package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="performance_reviews")
@Data @NoArgsConstructor @AllArgsConstructor
public class PerformanceReview {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="employee_id") private Employee employee;
    @Column(nullable=false) private String employeeName;
    @Column(nullable=false) private String userId;
    @Column(nullable=false) private String cycle;
    @Column(nullable=false, length=2000) private String goal;
    @Column(length=4000) private String selfComment;
    @Column(length=4000) private String managerComment;
    private Integer rating;
    @Column(nullable=false) private String status;
    private LocalDate dueDate;
    @Column(nullable=false) private LocalDateTime createdAt;
    @Column(nullable=false) private LocalDateTime updatedAt;
    @PrePersist void prePersist(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null) status="DRAFT"; }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
