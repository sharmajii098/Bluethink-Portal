package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="calendar_connections",uniqueConstraints=@UniqueConstraint(name="uq_calendar_owner_provider",columnNames={"owner_id","provider"}))
public class CalendarConnection {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Version public Long version;
 @Column(name="owner_id",nullable=false,length=80) public String ownerId;
 @Column(nullable=false,length=16) public String provider;
 @com.fasterxml.jackson.annotation.JsonIgnore @Column(columnDefinition="TEXT") public String encryptedAccessToken;
 @com.fasterxml.jackson.annotation.JsonIgnore @Column(columnDefinition="TEXT") public String encryptedRefreshToken;
 public OffsetDateTime accessExpiresAt;
 @Column(length=32,nullable=false) public String status="CONNECTED";
 public OffsetDateTime connectedAt;
 public OffsetDateTime lastSyncedAt;
 @Column(length=240) public String lastError;
}
