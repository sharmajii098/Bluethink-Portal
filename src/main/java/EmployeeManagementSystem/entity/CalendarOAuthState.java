package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="calendar_oauth_states",indexes=@Index(name="idx_calendar_state_expiry",columnList="expires_at"))
public class CalendarOAuthState {
 @Id @Column(length=64) public String stateHash;
 @Column(nullable=false,length=80) public String ownerId;
 @Column(nullable=false,length=16) public String provider;
 @Column(nullable=false,length=1000) public String encryptedVerifier;
 @Column(name="expires_at",nullable=false) public OffsetDateTime expiresAt;
 public boolean consumed;
}
