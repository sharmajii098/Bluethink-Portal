package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Data @Table(name="training_enrollments", uniqueConstraints=@UniqueConstraint(columnNames={"course_id","userId"}))
public class TrainingEnrollment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="course_id") private TrainingCourse course;
 @Column(nullable=false) private String userId;
 private String employeeName;
 private String status;
 private Integer progressPercent;
 private LocalDateTime enrolledAt;
 private LocalDateTime completedAt;
 private String certificateNumber;
 private String feedback;
 @PrePersist void pre(){ enrolledAt=LocalDateTime.now(); if(status==null) status="ENROLLED"; if(progressPercent==null) progressPercent=0; }
}
