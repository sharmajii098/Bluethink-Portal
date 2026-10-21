package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Maps both the current Team Chat fields and legacy required DB columns. */
@Entity
@Table(name = "chat_messages", indexes = @Index(name="idx_chat_message_conversation_time", columnList="conversation_id, created_at"))
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="conversation_id", nullable=false)
    private ChatConversation conversation;

    @Column(nullable=false, length=190)
    private String sender;

    // Both columns exist in the deployed schema and may be NOT NULL.
    @Column(name="content", nullable=false, length=5000)
    private String content;

    @Column(name="body", nullable=false, length=4000)
    private String body;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    public ChatMessage(ChatConversation conversation, String sender, String content) {
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.body = content;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void beforeInsert() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (body == null) body = content;
        if (content == null) content = body;
    }

    public Long getId() { return id; }
    public ChatConversation getConversation() { return conversation; }
    public String getSender() { return sender; }
    public String getContent() { return content == null ? body : content; }
    public String getBody() { return body == null ? content : body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
