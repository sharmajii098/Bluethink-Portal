package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="manager_one_on_ones", indexes={
 @Index(name="idx_one_on_one_manager", columnList="managerUserId"),
 @Index(name="idx_one_on_one_employee", columnList="employeeUserId"),
 @Index(name="idx_one_on_one_date", columnList="meetingDate")})
@Data
public class ManagerOneOnOne {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String meetingNumber;
 @Column(nullable=false) private String managerUserId;
 private String managerName;
 @Column(nullable=false) private String employeeUserId;
 private String employeeName;
 @Column(nullable=false) private LocalDate meetingDate;
 private String meetingTime;
 private String locationOrLink;
 private String agenda;
 private String discussionNotes;
 private String actionItems;
 private LocalDate nextFollowUpDate;
 @Column(nullable=false) private String status;
 private String cancellationReason;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){ if(status==null) status="SCHEDULED"; createdAt=LocalDateTime.now(); updatedAt=createdAt; }
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
