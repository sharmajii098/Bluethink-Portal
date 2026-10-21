package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Data @Table(name="support_ticket_comments")
public class SupportTicketComment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="ticket_id") private SupportTicket ticket;
 @Column(nullable=false) private String authorUserId;
 private String authorName;
 @Column(length=4000, nullable=false) private String comment;
 private LocalDateTime createdAt;
 @PrePersist void pre(){createdAt=LocalDateTime.now();}
}
