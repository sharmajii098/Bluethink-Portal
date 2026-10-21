package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "policy_acknowledgements", uniqueConstraints = @UniqueConstraint(columnNames = {"policy_id", "user_id"}))
public class PolicyAcknowledgement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;
    @Column(nullable = false)
    private boolean acknowledged;
    private LocalDateTime acknowledgedAt;
}
