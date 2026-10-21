package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishes", indexes = {
        @Index(name = "idx_wishes_receiver_created", columnList = "receiver_user_id, created_at"),
        @Index(name = "idx_wishes_sender_created", columnList = "sender_user_id, created_at")
})
@Getter
@Setter
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_user_id", nullable = false, length = 120)
    private String senderUserId;

    @Column(name = "receiver_user_id", nullable = false, length = 120)
    private String receiverUserId;

    @Column(name = "wish_type", nullable = false, length = 32)
    private String wishType;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Deterministic key prevents the same sender sending the same celebration wish twice.
    @Column(name = "wish_key", unique = true, length = 300)
    private String wishKey;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
