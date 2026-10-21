package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @Table(name="recruitment_candidates")
public class Candidate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="job_opening_id") private JobOpening jobOpening;
 @Column(nullable=false) private String fullName;
 @Column(nullable=false) private String email;
 private String phone;
 private String currentCompany;
 private String experience;
 private LocalDate dob;
 private String resumeUrl;
 private String stage;
 private String source;
 private LocalDate interviewDate;
 private String interviewer;
 private Integer interviewScore;
 @Column(length=3000) private String notes;
 private LocalDate offerDate;
 private LocalDate expectedJoiningDate;
 private Long employeeId;
 private Long applicantEmployeeId;
 private Long referredByEmployeeId;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; if(stage==null) stage="APPLIED"; }
 @PreUpdate void upd(){ updatedAt=LocalDateTime.now(); }
}
