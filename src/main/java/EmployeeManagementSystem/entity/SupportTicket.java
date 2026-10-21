package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Data @Table(name="support_tickets", uniqueConstraints=@UniqueConstraint(name="uk_ticket_number", columnNames="ticketNumber"))
public class SupportTicket {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String ticketNumber;
 @Column(nullable=false) private String userId;
 private String employeeName;
 @Column(nullable=false) private String subject;
 @Column(length=4000, nullable=false) private String description;
 private String category;
 private String priority;
 private String status;
 private String assignedTo;
 private String assignedTeam;
 private LocalDateTime slaDueAt;
 private LocalDateTime resolvedAt;
 private String resolution;
 private String lastComment;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();updatedAt=createdAt;if(priority==null)priority="MEDIUM";if(status==null)status="OPEN";if(assignedTeam==null)assignedTeam="IT SUPPORT";}
 @PreUpdate void upd(){updatedAt=LocalDateTime.now();}
}
