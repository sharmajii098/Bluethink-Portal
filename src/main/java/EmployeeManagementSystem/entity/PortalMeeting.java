package EmployeeManagementSystem.entity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="portal_meetings",indexes={@Index(name="idx_portal_meeting_starts",columnList="starts_at")})
public class PortalMeeting {
 @Version public Long version;
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=160) public String title;
 @Column(nullable=false) public OffsetDateTime startsAt;
 @Column(nullable=false) public OffsetDateTime endsAt;
 @Column(nullable=false,length=500) public String meetingUrl;
 @Column(nullable=false,length=80) public String organizer;
 @Column(nullable=false,length=20) public String status="SCHEDULED";
 @Column(length=1200) public String agenda;
 @Column(length=36) public String seriesId;
 @Column(length=80) public String timeZone="Asia/Kolkata";
 @Column(nullable=false) public int calendarSequence=0;
 @Column(nullable=false) public OffsetDateTime updatedAt=OffsetDateTime.now();
 @PreUpdate public void updated(){updatedAt=OffsetDateTime.now();}
}
