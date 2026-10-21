package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="onboarding_tasks")
public class OnboardingTask {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long employeeId;
 private String employeeName;
 @Column(nullable=false) private String title;
 @Column(length=2000) private String description;
 private String category;
 private LocalDate dueDate;
 private String status;
 private String assignedTo;
 private LocalDate completedDate;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null) status="PENDING"; }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
