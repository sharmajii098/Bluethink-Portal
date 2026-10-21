package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Data
public class Announcement {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 @Column(nullable=false, length=8000) private String content;
 private String category;
 private String priority;
 private LocalDateTime publishAt;
 private LocalDateTime expiresAt;
 private String audienceRole;
 private String audienceDepartment;
 private String createdBy;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(priority==null) priority="NORMAL"; if(publishAt==null) publishAt=createdAt; }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
