package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Data
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"announcementId","userId"}))
public class AnnouncementRead {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long announcementId;
 @Column(nullable=false) private String userId;
 private LocalDateTime readAt;
 private boolean acknowledged;
}
