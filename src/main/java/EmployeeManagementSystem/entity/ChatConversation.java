package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/** Supports both current Team Chat columns and legacy required columns. */
@Entity
@Table(name = "chat_conversations")
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    // Existing employees_db schema has name VARCHAR(120) NOT NULL.
    @Column(name = "name", nullable = false, length = 120)
    private String legacyName;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "created_by", nullable = false, length = 190)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_conversation_members", joinColumns = @JoinColumn(name = "conversation_id"))
    @Column(name = "username", nullable = false, length = 190)
    private Set<String> members = new LinkedHashSet<>();

    public ChatConversation() {}

    public ChatConversation(String title, String type, String createdBy, Set<String> members) {
        this.title = title;
        this.legacyName = title;
        this.type = type;
        this.createdBy = createdBy;
        this.members = members == null ? new LinkedHashSet<>() : new LinkedHashSet<>(members);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PrePersist
    protected void beforeInsert() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = createdAt;
        if (legacyName == null || legacyName.isBlank()) legacyName = title;
        if (title == null || title.isBlank()) title = legacyName;
    }

    @PreUpdate
    protected void beforeUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title == null || title.isBlank() ? legacyName : title; }
    public String getName() { return legacyName == null || legacyName.isBlank() ? title : legacyName; }
    public String getType() { return type; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<String> getMembers() { return members; }
}
