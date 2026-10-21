package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="calendar_event_links",uniqueConstraints=@UniqueConstraint(name="uq_calendar_link",columnNames={"connection_id","meeting_id"}))
public class CalendarEventLink {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Version public Long version;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="connection_id",nullable=false) public CalendarConnection connection;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="meeting_id",nullable=false) public PortalMeeting meeting;
 @Column(length=1000) public String remoteId;
 public int lastSequence=-1;
 public int attemptedSequence=-1;
 @Column(length=24,nullable=false) public String status="PENDING";
 public int attempts;
 public OffsetDateTime nextAttemptAt;
 public OffsetDateTime syncedAt;
 @Column(length=240) public String lastError;
}
