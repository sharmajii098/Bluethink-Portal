package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="training_courses")
public class TrainingCourse {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 @Column(length=4000) private String description;
 private String category;
 private String instructor;
 private String level;
 private Integer durationHours;
 private LocalDate startDate;
 private LocalDate endDate;
 private String status;
 private String certificateName;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null) status="PUBLISHED"; }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
