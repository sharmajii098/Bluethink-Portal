package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;
    private String activity;
    private String status;

    @Column(length = 80)
    private String userId;
    @Column(length = 40)
    private String role;
    @Column(length = 80)
    private String module;
    @Column(length = 60)
    private String action;
    @Column(length = 1000)
    private String details;
    @Column(length = 80)
    private String ipAddress;
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
