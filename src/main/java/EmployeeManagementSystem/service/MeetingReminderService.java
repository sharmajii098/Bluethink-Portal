package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.*;
import EmployeeManagementSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class MeetingReminderService {

    private final MeetingReminderRepository reminders;
    private final PortalMeetingRepository meetings;
    private final PortalActor actor;
    private final MeetingRecipients recipients;
    public MeetingReminderService(MeetingReminderRepository reminders,PortalMeetingRepository meetings,
          PortalActor actor,MeetingRecipients recipients) {
        this.reminders=reminders;this.meetings=meetings;this.actor=actor;this.recipients=recipients;
    }
    public record Request(List<String> recipients,int minutesBefore) {}
    public record Summary(Long id,String recipient,OffsetDateTime sendAt,String status,int attempts,String kind,String lastError) {}
    private Summary summary(MeetingReminder r) { return new Summary(r.id,r.recipient,r.sendAt,r.status,r.attempts,r.kind,r.lastError); }
    private PortalMeeting editable(Long id) {
        PortalMeeting m=meetings.lock(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));actor.edit(m);return m;
    }
    private void upcoming(PortalMeeting m) {
        if("CANCELLED".equals(m.status)||!m.startsAt.isAfter(OffsetDateTime.now()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Meeting must be upcoming");
    }
    private MeetingReminder enqueue(PortalMeeting m,String email,String kind,Integer minutes,String owner) {
        String key=kind+":"+m.calendarSequence+(minutes==null?"":":"+minutes);
        if(reminders.existsByMeetingIdAndRecipientAndDeliveryKey(m.id,email,key))return null;
        MeetingReminder r=new MeetingReminder();r.meeting=m;r.recipient=email;r.kind=kind;r.minutesBefore=minutes;
        r.sendAt=minutes==null?OffsetDateTime.now():m.startsAt.minusMinutes(minutes);
        r.nextAttemptAt=r.sendAt;r.deliveryKey=key;r.meetingSequence=m.calendarSequence;r.createdBy=owner;
        return reminders.save(r);
    }
    @Transactional public List<Summary> schedule(Long id,Request input) {
        PortalMeeting m=editable(id);upcoming(m);
        if(input==null||!Set.of(15,30,60,1440).contains(input.minutesBefore()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose 15, 30, 60 or 1440 minutes");
        if(!m.startsAt.minusMinutes(input.minutesBefore()).isAfter(OffsetDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Reminder time must be in the future");
        Set<String> addresses=recipients.validate(input.recipients());
        for(String email:addresses)enqueue(m,email,"REMINDER",input.minutesBefore(),actor.id());
        return reminders.findByMeetingIdOrderBySendAtAsc(id).stream().map(this::summary).toList();
    }
    @Transactional public Map<String,Object> invite(Long id,List<String> input) {
        PortalMeeting m=editable(id);upcoming(m);Set<String> addresses=recipients.validate(input);int queued=0;
        for(String email:addresses)if(enqueue(m,email,"INVITE",null,actor.id())!=null)queued++;
        return Map.of("meetingId",id,"queued",queued,"duplicateRequestsIgnored",addresses.size()-queued,
            "status","QUEUED","message","Queued in outbox; SMTP acceptance is tracked separately");
    }
    @Transactional(readOnly=true) public List<Summary> list(Long id) {
        PortalMeeting m=meetings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));actor.edit(m);
        return reminders.findByMeetingIdOrderBySendAtAsc(id).stream().map(this::summary).toList();
    }
    /** Call while holding the meeting lock, after incrementing the calendar sequence. */
    @Transactional public void meetingChanged(PortalMeeting m) {
        List<MeetingReminder> old=reminders.findByMeetingIdOrderBySendAtAsc(m.id);
        Set<String> invitees=new LinkedHashSet<>();Set<String> reminderPairs=new LinkedHashSet<>();
        for(MeetingReminder r:old){
            if(!"REMINDER".equals(r.kind))invitees.add(r.recipient);
            if("REMINDER".equals(r.kind)&&r.minutesBefore!=null)reminderPairs.add(r.recipient+"|"+r.minutesBefore);
            if("PENDING".equals(r.status))r.status="CANCELLED";
        }
        reminders.saveAll(old);reminders.flush();
        boolean cancelled="CANCELLED".equals(m.status);
        for(String email:invitees)enqueue(m,email,cancelled?"CANCEL":"UPDATE",null,actor.id());
        if(!cancelled)for(String pair:reminderPairs){int split=pair.lastIndexOf('|');int minutes=Integer.parseInt(pair.substring(split+1));
            if(m.startsAt.minusMinutes(minutes).isAfter(OffsetDateTime.now()))enqueue(m,pair.substring(0,split),"REMINDER",minutes,actor.id());}
    }
    @Transactional public Summary retry(Long id,String reason,boolean acceptDuplicateRisk) {
        MeetingReminder initial=reminders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        editable(initial.meeting.id);
        MeetingReminder r=reminders.lock(id).orElseThrow();
        if(!Set.of("FAILED","DELIVERY_UNKNOWN").contains(r.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Only failed or uncertain deliveries may be retried");
        if(reason==null||reason.trim().length()<10||reason.length()>400)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Provide a review reason (10-400 characters)");
        if("DELIVERY_UNKNOWN".equals(r.status)&&!acceptDuplicateRisk)throw new ResponseStatusException(HttpStatus.CONFLICT,"Review provider logs and acknowledge duplicate-delivery risk");
        if(!"CANCEL".equals(r.kind))upcoming(r.meeting);
        if(r.meeting.calendarSequence!=r.meetingSequence)throw new ResponseStatusException(HttpStatus.CONFLICT,"Use the latest meeting update instead");
        r.status="PENDING";r.nextAttemptAt=OffsetDateTime.now();r.retryReason=(actor.id()+": "+reason.trim()).substring(0,Math.min(400,(actor.id()+": "+reason.trim()).length()));r.attempts=0;r.lastError=null;r.claimToken=null;
        return summary(reminders.save(r));
    }
}
