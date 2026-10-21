package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

/** Durable invitation/reminder outbox; SMTP acceptance is not proof of inbox delivery. */
@Entity
@Table(name="portal_meeting_reminders", indexes=@Index(name="idx_meeting_outbox_due",columnList="status,next_attempt_at"),
    uniqueConstraints=@UniqueConstraint(name="uq_meeting_delivery_key",columnNames={"meeting_id","recipient","delivery_key"}))
public class MeetingReminder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Version public Long version;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="meeting_id",nullable=false) public PortalMeeting meeting;
    @Column(nullable=false,length=254) public String recipient;
    @Column(name="send_at",nullable=false) public OffsetDateTime sendAt;
    @Column(name="next_attempt_at") public OffsetDateTime nextAttemptAt;
    @Column(nullable=false,length=24) public String status="PENDING";
    @Column(nullable=false) public int attempts=0;
    @Column(length=400) public String lastError;
    public OffsetDateTime sentAt;
    @Column(name="delivery_key",nullable=false,length=100) public String deliveryKey;
    @Column(nullable=false,length=16) public String kind="REMINDER";
    public Integer minutesBefore;
    public int meetingSequence;
    public OffsetDateTime claimedAt;
    @Column(length=36) public String claimToken;
    @Column(length=80) public String createdBy;
    @Column(length=400) public String retryReason;
}
