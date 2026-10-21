package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="job_openings")
public class JobOpening {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 private String department;
 private String location;
 private String employmentType;
 private String experience;
 @Column(length=5000) private String description;
 private LocalDate openingDate;
 private LocalDate closingDate;
 private String status;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(status==null) status="OPEN"; if(openingDate==null) openingDate=LocalDate.now(); }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
