package EmployeeManagementSystem.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "account_invitations", indexes = @Index(name = "idx_invitation_account", columnList = "account_id"))
public class AccountInvitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    @Column(nullable = false, length = 64, unique = true)
    private String tokenHash;
    @Column(nullable = false)
    private Instant expiresAt;
    private Instant consumedAt;
    private Instant createdAt;
    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getConsumedAt() { return consumedAt; }
    public void setConsumedAt(Instant consumedAt) { this.consumedAt = consumedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
