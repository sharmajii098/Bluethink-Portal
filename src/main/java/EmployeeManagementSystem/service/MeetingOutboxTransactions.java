package EmployeeManagementSystem.service;
import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import java.time.OffsetDateTime;
import java.util.*;

/** A claim commits before SMTP is called. A crash after acceptance is quarantined, not blindly re-sent. */
@Service
public class MeetingOutboxTransactions {
    private final MeetingReminderRepository rows;private final PortalMeetingRepository meetings;
    public MeetingOutboxTransactions(MeetingReminderRepository rows,PortalMeetingRepository meetings){this.rows=rows;this.meetings=meetings;}
    public record Claim(Long id,String token,String recipient,String kind,int attempt,Long meetingId,String title,
          String agenda,String url,OffsetDateTime start,OffsetDateTime end,int sequence){}
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Claim claim(Long id) {
        MeetingReminder preliminary=rows.findById(id).orElse(null);if(preliminary==null)return null;
        meetings.lock(preliminary.meeting.id).orElseThrow();
        MeetingReminder r=rows.lock(id).orElseThrow();OffsetDateTime now=OffsetDateTime.now();
        if(!"PENDING".equals(r.status)||r.sendAt.isAfter(now)||(r.nextAttemptAt!=null&&r.nextAttemptAt.isAfter(now)))return null;
        PortalMeeting m=r.meeting;
        if(r.meetingSequence!=m.calendarSequence||(!"CANCEL".equals(r.kind)&&("CANCELLED".equals(m.status)||!m.startsAt.isAfter(now)))){r.status="CANCELLED";return null;}
        r.status="SENDING";r.claimedAt=now;r.claimToken=UUID.randomUUID().toString();r.attempts++;
        return new Claim(r.id,r.claimToken,r.recipient,r.kind,r.attempts,m.id,m.title,m.agenda,m.meetingUrl,m.startsAt,m.endsAt,m.calendarSequence);
    }
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void finish(Claim claim,String outcome) {
        MeetingReminder r=rows.lock(claim.id()).orElse(null);
        if(r==null||!"SENDING".equals(r.status)||!Objects.equals(r.claimToken,claim.token()))return;
        r.status=outcome;
        if("SENT".equals(outcome)){r.sentAt=OffsetDateTime.now();r.lastError=null;}
        else if("RETRY".equals(outcome)){r.status=r.attempts>=3?"FAILED":"PENDING";r.nextAttemptAt=OffsetDateTime.now().plusMinutes(1L<<Math.min(r.attempts,6));r.lastError="SMTP authentication or connection failure before send; check configuration";}
        else r.lastError="SMTP outcome is uncertain. Review provider logs before a manual retry";
    }
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void quarantine(Long id) {
        MeetingReminder r=rows.lock(id).orElse(null);
        if(r!=null&&"SENDING".equals(r.status)&&r.claimedAt.isBefore(OffsetDateTime.now().minusMinutes(10))){r.status="DELIVERY_UNKNOWN";r.lastError="Worker ended without recording SMTP outcome; manual review required";}
    }
}
